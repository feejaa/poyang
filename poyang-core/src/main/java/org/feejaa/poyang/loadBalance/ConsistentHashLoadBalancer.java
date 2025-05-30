package org.feejaa.poyang.loadBalance;

import cn.hutool.core.collection.CollUtil;
import org.feejaa.poyang.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ConsistentHashLoadBalancer implements LoadBalancer {

    private final TreeMap<Integer, ServiceMetaInfo> virtualNodes = new TreeMap<>();

    private static final int VIRTUAL_NODE_NUM = 10;

    /**
     * 选择服务元信息
     *
     * @param params 请求参数
     * @param serviceMetaInfoList 服务元信息列表
     * @return 选中的服务元信息
     */
    @Override
    public ServiceMetaInfo select(Map<String, Object> params, List<ServiceMetaInfo> serviceMetaInfoList) {
        if (CollUtil.isEmpty(serviceMetaInfoList)) {
            return null;
        }
        for (ServiceMetaInfo serviceMetaInfo : serviceMetaInfoList) {
            for (int i = 0; i < VIRTUAL_NODE_NUM; i++) {
                int hash = hash(serviceMetaInfo);
                virtualNodes.put(hash, serviceMetaInfo);
            }
        }

        int hash = hash(params);
        Map.Entry<Integer, ServiceMetaInfo> entry = virtualNodes.ceilingEntry(hash);
        if (entry == null) {
            entry = virtualNodes.firstEntry();
        }
        return entry.getValue();
    }

    /**
     * 计算对象的哈希值
     *
     * @param object 对象
     * @return 哈希值
     */
    private int hash(Object object) {
        return object.hashCode();
    }
}
