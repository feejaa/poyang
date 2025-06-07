package org.feejaa.poyang.bootstrap;

import org.feejaa.poyang.PoYangApplication;
import org.feejaa.poyang.config.PoYangConfig;
import org.feejaa.poyang.config.RegistryConfig;
import org.feejaa.poyang.model.ServiceMetaInfo;
import org.feejaa.poyang.model.ServiceRegisterInfo;
import org.feejaa.poyang.registry.LocalRegistry;
import org.feejaa.poyang.registry.Registry;
import org.feejaa.poyang.registry.RegistryFactory;
import org.feejaa.poyang.server.tcp.TcpServer;

import java.util.List;

/**
 * 服务方初始化
 */
public class ProviderBootStrap {

    /**
     * 初始化服务提供方
     *
     * @param serviceRegisterInfoList 服务注册信息列表
     */
    public static void init(List<ServiceRegisterInfo<?>> serviceRegisterInfoList) {
        // 初始化应用
        PoYangApplication.init();

        final PoYangConfig.PoYang poyang = PoYangApplication.getRpcConfig().getPoyang();

        // 注册服务
        for (ServiceRegisterInfo<?> serviceRegisterInfo : serviceRegisterInfoList) {
            String serviceName = serviceRegisterInfo.getServiceName();
            // 设置服务注册信息
            LocalRegistry.register(serviceName, serviceRegisterInfo.getImplClass());

            RegistryConfig registryConfig = PoYangApplication.getRpcConfig().getRegistryConfig();
            Registry registry = RegistryFactory.getRegistry(registryConfig.getRegistryType());
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(serviceName);
            serviceMetaInfo.setServiceHost(poyang.getServerHost());
            serviceMetaInfo.setServicePort(poyang.getPort());
            try {
                // 注册服务到注册中心
                registry.register(serviceMetaInfo);
            } catch (Exception e) {
                throw new RuntimeException("Failed to register service: " + serviceName, e);
            }
        }

        // 启动服务

        new TcpServer().doStart(poyang.getPort());
    }
}
