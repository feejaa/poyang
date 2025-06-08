package org.feejaa.boot;

import lombok.extern.slf4j.Slf4j;
import org.feejaa.base.PoYangOutflow;
import org.feejaa.poyang.PoYangApplication;
import org.feejaa.poyang.config.PoYangConfig;
import org.feejaa.poyang.config.RegistryConfig;
import org.feejaa.poyang.model.ServiceMetaInfo;
import org.feejaa.poyang.registry.LocalRegistry;
import org.feejaa.poyang.registry.Registry;
import org.feejaa.poyang.registry.RegistryFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

@Slf4j
public class PoYangOutflowInitPostProcessor implements BeanPostProcessor {


    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

        Class<?> beanClass = bean.getClass();
        PoYangOutflow declaredAnnotation = beanClass.getAnnotation(PoYangOutflow.class);
        if (declaredAnnotation == null) {
            return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
        }
        log.info("Found PoYangOutflow annotation on bean: {}", beanName);
        // 不为空则为服务提供方
        // 1、获取服务基本信息
        Class<?> interfaceClass = declaredAnnotation.interfaceClass();

        if (interfaceClass == void.class) {
            interfaceClass = beanClass.getInterfaces()[0];
        }
        // 2 注册服务
        String serviceVersion = declaredAnnotation.serviceVersion();

        final PoYangConfig.PoYang poyang = PoYangApplication.getRpcConfig().getPoyang();

        // 注册服务
        String serviceName = interfaceClass.getName();
        // 设置服务注册信息
        LocalRegistry.register(serviceName, beanClass);

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

        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);

    }

}
