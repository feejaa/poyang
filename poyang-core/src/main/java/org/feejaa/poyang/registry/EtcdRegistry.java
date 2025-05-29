package org.feejaa.poyang.registry;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hutool.core.util.StrUtil;
import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;
import cn.hutool.json.JSONUtil;
import io.etcd.jetcd.*;
import io.etcd.jetcd.kv.GetResponse;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;
import io.etcd.jetcd.watch.WatchEvent;
import lombok.extern.slf4j.Slf4j;
import org.feejaa.poyang.config.RegistryConfig;
import org.feejaa.poyang.model.ServiceMetaInfo;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Slf4j
public class EtcdRegistry implements Registry {

    /**
     * 根节点
     */
    public static final String ETCD_ROOT_PATH = "/rpc/";

    /**
     * 正在监听的 key 集合
     */
    private final Set<String> watchingKeySet = new ConcurrentHashSet<>();

    private Client client;

    private KV kvClient;

    /**
     * 本机注册的节点 key 集合（用于维护续期）
     */
    private final Set<String> localRegisterNodeKeySet = new HashSet<>();

    private final RegistryServiceCache registryServiceCache = new RegistryServiceCache();


    // 重写init方法，传入RegistryConfig参数
    @Override
    public void init(final RegistryConfig registryConfig) {

        log.info("etcd registry init: {}", registryConfig);
        // 使用Client.builder()创建一个Client对象，设置endpoints为registryConfig.getAddress()，设置connectTimeout为registryConfig.getTimeout()
        client = Client.builder().endpoints(registryConfig.getAddress())
                .connectTimeout(Duration.ofMillis(registryConfig.getTimeout())).build();
        // 获取KVClient对象
        kvClient = client.getKVClient();
        // 启动定时任务
        heartBeat();

    }

    /**
     * 注册服务
     *
     * @param serviceMetaInfo
     * @throws Exception
     */
    @Override
    public void register(ServiceMetaInfo serviceMetaInfo) throws Exception {


        // 创建 lease 和 KV 客户端
        Lease leaseClient = client.getLeaseClient();

        // 创建一个 30 秒的租约
        long leaseId = leaseClient.grant(30).get().getID();

        String registerKey = ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey();
        // 将服务信息转换为 ByteSequence
        ByteSequence key = ByteSequence.from(registerKey, StandardCharsets.UTF_8);
        ByteSequence value = ByteSequence.from(JSONUtil.toJsonStr(serviceMetaInfo), StandardCharsets.UTF_8);

        // 创建 PutOption，设置租约 ID
        PutOption putOption = PutOption.builder().withLeaseId(leaseId).build();
        // 将服务信息存入 KV 客户端
        kvClient.put(key, value, putOption).get();

        // 缓存到本地
        localRegisterNodeKeySet.add(registerKey);
    }

    /**
     * @param serviceMetaInfo
     */
    @Override
    public void unRegister(ServiceMetaInfo serviceMetaInfo) {
        String key = ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey();
        kvClient.delete(ByteSequence.from(key, StandardCharsets.UTF_8));

        localRegisterNodeKeySet.remove(key);
    }

    @Override
    public List<ServiceMetaInfo> discover(String serviceKey) {

        // search
        StringBuilder searchPrefix = new StringBuilder(ETCD_ROOT_PATH);

        if (StrUtil.isNotBlank(serviceKey)) {
            searchPrefix.append(serviceKey).append("/");
        }

        try {
            GetOption getOption = GetOption.builder().isPrefix(true).build();
            List<KeyValue> kvs = kvClient.get(ByteSequence.from(searchPrefix.toString(), StandardCharsets.UTF_8), getOption)
                    .get()
                    .getKvs();
            List<ServiceMetaInfo> serviceMetaInfoList = kvs
                    .stream()
                    .map(kvPair -> {
                        String k = kvPair.getKey().toString(StandardCharsets.UTF_8);
                        watch(k);
                        String v = kvPair.getValue().toString(StandardCharsets.UTF_8);
                        return JSONUtil.toBean(v, ServiceMetaInfo.class);
                    }).collect(Collectors.toList());
            registryServiceCache.writeCache(serviceMetaInfoList);

            return serviceMetaInfoList;
        } catch (Exception e) {
            throw new RuntimeException("获取服务列表失败", e);
        }
    }

    @Override
    public void destroy() {
        log.info("当前节点下线");
        for (String key : localRegisterNodeKeySet) {
            try {
                kvClient.delete(ByteSequence.from(key, StandardCharsets.UTF_8)).get();
            } catch (Exception e) {
                throw new RuntimeException(key + "节点下线失败", e);
            }
        }
        // 释放资源
        if (kvClient != null) {
            kvClient.close();
        }
        if (client != null) {
            client.close();
        }
    }

    @Override
    public void heartBeat() {

        // 5 s 续签一次
        CronUtil.schedule("*/5 * * * * *", (Task) () -> {
            for (String key : localRegisterNodeKeySet) {
                try {
                    List<KeyValue> kvPairs = kvClient.get(ByteSequence.from(key, StandardCharsets.UTF_8))
                            .get()
                            .getKvs();
                    // 节点已过期
                    if (CollUtil.isEmpty(kvPairs)) {
                        continue;
                    }
                    // 节点未过期
                    KeyValue keyValue = kvPairs.get(0);
                    String value = keyValue.getValue().toString(StandardCharsets.UTF_8);
                    ServiceMetaInfo serviceMetaInfo = JSONUtil.toBean(value, ServiceMetaInfo.class);
                    register(serviceMetaInfo);
                } catch (Exception e) {
                    throw new RuntimeException(key + "续签失败", e);
                }
            }
        });

        CronUtil.setMatchSecond(true);
        CronUtil.start();
    }

    @Override
    public void watch(String serviceNodeKey) {
        Watch watchClient = client.getWatchClient();
        boolean newWatch = watchingKeySet.add(serviceNodeKey);
        if (newWatch) {
            watchClient.watch(ByteSequence.from(serviceNodeKey, StandardCharsets.UTF_8), response -> {
                for (WatchEvent event : response.getEvents()) {
                    log.info("watch event:{}", event);
                    switch (event.getEventType()) {
                        case PUT:
                            // 服务上线
                            break;
                        case DELETE:
                            log.info("服务下线:{}", serviceNodeKey);
                            registryServiceCache.clearCache();
                        default:
                            break;
                    }
                }
            });
        }
    }

    public static void main(String[] args) {

        Client client = Client.builder().endpoints("http://localhost:2379").build();

        KV kvClient = client.getKVClient();
        ByteSequence key = ByteSequence.from("test_key".getBytes());
        ByteSequence value = ByteSequence.from("test_value".getBytes());

        kvClient.put(key, value);

        CompletableFuture<GetResponse> getResponseCompletableFuture = kvClient.get(key);

        try {
            GetResponse getResponse = getResponseCompletableFuture.get();
            log.info("resp:{}", getResponse);
        } catch (InterruptedException e) {


        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }

        kvClient.delete(key);

    }
}
