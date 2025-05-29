package org.feejaa.poyang.example;

import org.feejaa.poyang.PoYangApplication;
import org.feejaa.poyang.config.PoYangConfig;
import org.feejaa.poyang.config.RegistryConfig;
import org.feejaa.poyang.model.ServiceMetaInfo;
import org.feejaa.poyang.registry.Registry;
import org.feejaa.poyang.registry.RegistryFactory;
import org.feejaa.poyang.server.VertxHttpServer;
import org.feejaa.poyang.service.UserService;

public class ProviderExample {

    public static void main(String[] args) {

        PoYangApplication.init();

        String serviceName = UserService.class.getName();

        PoYangConfig rpcConfig = PoYangApplication.getRpcConfig();
        RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
        Registry registry = RegistryFactory.getRegistry(registryConfig.getRegistryType());

        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName(serviceName);
        serviceMetaInfo.setServiceHost(rpcConfig.getPoyang().getServerHost());
        serviceMetaInfo.setServicePort(rpcConfig.getPoyang().getPort());

        try {
            registry.register(serviceMetaInfo);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        VertxHttpServer vertxHttpServer = new VertxHttpServer();
        vertxHttpServer.doStart(rpcConfig.getPoyang().getPort());

    }
}
