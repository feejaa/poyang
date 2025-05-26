package org.feejaa.poyang.registry;

import cn.hutool.json.JSONUtil;
import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.KV;
import io.etcd.jetcd.Lease;
import io.etcd.jetcd.kv.GetResponse;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;
import lombok.extern.slf4j.Slf4j;
import org.feejaa.poyang.config.RegistryConfig;
import org.feejaa.poyang.model.ServiceMeatInfo;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Slf4j
public class EtcdRegistry implements Registry {

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

    private Client client;

    private KV kvClient;

    /**
     * 根节点
     */
    private static final String ETCD_ROOT_PATH = "/rpc/";


    @Override
    // 重写init方法，传入RegistryConfig参数
    public void init(final RegistryConfig registryConfig) {

        log.info("etcd registry init: {}", registryConfig);
        // 使用Client.builder()创建一个Client对象，设置endpoints为registryConfig.getAddress()，设置connectTimeout为registryConfig.getTimeout()
        client = Client.builder().endpoints(registryConfig.getAddress())
                .connectTimeout(Duration.ofMillis(registryConfig.getTimeout())).build();
        // 获取KVClient对象
        kvClient = client.getKVClient();
    }

    /**
     * 注册服务
     *
     * @param serviceMeatInfo
     * @throws Exception
     */
    @Override
    public void register(ServiceMeatInfo serviceMeatInfo) throws Exception {
        // 创建 lease 和 KV 客户端
        Lease leaseClient = client.getLeaseClient();

        // 创建一个 30 秒的租约
        long leaseId = leaseClient.grant(30).get().getID();

        // 将服务信息转换为 ByteSequence
        ByteSequence key = ByteSequence.from(serviceMeatInfo.getServiceKey(), StandardCharsets.UTF_8);
        ByteSequence value = ByteSequence.from(JSONUtil.toJsonStr(serviceMeatInfo), StandardCharsets.UTF_8);

        // 创建 PutOption，设置租约 ID
        PutOption putOption = PutOption.builder().withLeaseId(leaseId).build();
        // 将服务信息存入 KV 客户端
        kvClient.put(key, value, putOption).get();
    }

    /**
     * @param serviceMeatInfo
     */
    @Override
    public void unRegister(ServiceMeatInfo serviceMeatInfo) {
        kvClient.delete(ByteSequence.from(ETCD_ROOT_PATH + serviceMeatInfo.getServiceKey(), StandardCharsets.UTF_8));
    }

    @Override
    public List<ServiceMeatInfo> discover(String serviceKey) {
        // search
        String searchPrefix = ETCD_ROOT_PATH + serviceKey + "/";

        try {
            GetOption getOption = GetOption.builder().isPrefix(true).build();
            return kvClient.get(ByteSequence.from(searchPrefix, StandardCharsets.UTF_8), getOption)
                    .get().getKvs().stream().map(kvPair -> {
                        String v = kvPair.getValue().toString(StandardCharsets.UTF_8);
                        return JSONUtil.toBean(v, ServiceMeatInfo.class);
                    }).collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("获取服务列表失败", e);
        }
    }

    @Override
    public void destroy() {
        log.info("当前节点下线");
        // 释放资源
        if (kvClient!= null) {
            kvClient.close();
        }
        if (client != null) {
            client.close();
        }
    }
}
