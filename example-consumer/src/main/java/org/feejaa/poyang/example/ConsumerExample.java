package org.feejaa.poyang.example;

import lombok.extern.slf4j.Slf4j;
import org.feejaa.poyang.PoYangApplication;
import org.feejaa.poyang.config.PoYangConfig;
import org.feejaa.poyang.config.RegistryConfig;
import org.feejaa.poyang.model.ServiceMetaInfo;
import org.feejaa.poyang.model.User;
import org.feejaa.poyang.proxy.ServiceProxyFacotry;
import org.feejaa.poyang.registry.Registry;
import org.feejaa.poyang.registry.RegistryFactory;
import org.feejaa.poyang.service.UserService;

import java.util.List;

@Slf4j
public class ConsumerExample {

    public static void main(String[] args) {
/*

        UserService userService = ServiceProxyFacotry.getProxy(UserService.class);
        User user = new User();
        user.setName("poyang");
        // 调用
        User newUser = userService.getUser(user);
        if (newUser != null) {
            log.info("newUser:{}", newUser);
        } else {
            log.info("newUser is null");
        }
        Integer userCount = userService.getUserCount();
        log.info("userCount:{}", userCount);
*/

        PoYangApplication.init();

        PoYangConfig rpcConfig = PoYangApplication.getRpcConfig();
        RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
        Registry registry = RegistryFactory.getRegistry(registryConfig.getRegistryType());

        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName(UserService.class.getName());
        serviceMetaInfo.setServiceHost(rpcConfig.getPoyang().getServerHost());
        serviceMetaInfo.setServicePort(rpcConfig.getPoyang().getPort());
        List<ServiceMetaInfo> serviceMetaInfoList = registry.discover(serviceMetaInfo.getServiceKey());

        log.info("serviceMetaInfoList:{}", serviceMetaInfoList);
//        ServiceMetaInfo proxy = ServiceProxyFacotry.getProxy(serviceMetaInfoList.get(0).getClass());
        // 获得代理信息，发送 http 或者其他方式建连接


    }
}
