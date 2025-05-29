package org.feejaa.poyang.registry;

import org.feejaa.poyang.config.RegistryConfig;
import org.feejaa.poyang.model.ServiceMetaInfo;

import java.util.List;

public interface Registry {

    void init(RegistryConfig registryConfig);

    void register(ServiceMetaInfo serviceMetaInfo) throws Exception;

    void unRegister(ServiceMetaInfo serviceMetaInfo);

    List<ServiceMetaInfo> discover(String serviceKey);

    void destroy();

    void heartBeat();

    /**
     * 监听——消费端
     * @param serviceKey
     */
    void watch(String serviceKey);

}
