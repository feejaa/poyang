package org.feejaa.poyang.example;

import org.feejaa.poyang.PoYangApplication;
import org.feejaa.poyang.config.PoYangConfig;
import org.feejaa.poyang.config.RegistryConfig;
import org.feejaa.poyang.example.service.UserServiceImpl;
import org.feejaa.poyang.model.ServiceMetaInfo;
import org.feejaa.poyang.registry.LocalRegistry;
import org.feejaa.poyang.registry.Registry;
import org.feejaa.poyang.registry.RegistryFactory;
import org.feejaa.poyang.server.tcp.TcpServer;
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
            LocalRegistry.register(serviceName, UserServiceImpl.class);

            registry.register(serviceMetaInfo);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        TcpServer server = new TcpServer();
        server.doStart(rpcConfig.getPoyang().getPort());

    }
}
