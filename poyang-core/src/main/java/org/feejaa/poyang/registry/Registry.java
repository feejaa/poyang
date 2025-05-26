package org.feejaa.poyang.registry;

import org.feejaa.poyang.config.RegistryConfig;
import org.feejaa.poyang.model.ServiceMeatInfo;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface Registry {

    void init(RegistryConfig registryConfig);

    void register(ServiceMeatInfo serviceMeatInfo) throws Exception;

    void unRegister(ServiceMeatInfo serviceMeatInfo);

    List<ServiceMeatInfo> discover(String serviceKey);

    void destroy();
}
