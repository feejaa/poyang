package org.feejaa.poyang.example;

import org.feejaa.poyang.PoYangApplication;
import org.feejaa.poyang.bootstrap.ProviderBootStrap;
import org.feejaa.poyang.config.PoYangConfig;
import org.feejaa.poyang.config.RegistryConfig;
import org.feejaa.poyang.example.service.UserServiceImpl;
import org.feejaa.poyang.model.ServiceMetaInfo;
import org.feejaa.poyang.model.ServiceRegisterInfo;
import org.feejaa.poyang.registry.LocalRegistry;
import org.feejaa.poyang.registry.Registry;
import org.feejaa.poyang.registry.RegistryFactory;
import org.feejaa.poyang.server.tcp.TcpServer;
import org.feejaa.poyang.service.UserService;

import java.util.ArrayList;
import java.util.List;

public class ProviderExample {

    public static void main(String[] args) {

        List<ServiceRegisterInfo<?>> serviceRegisterInfos = new ArrayList<>();
        ServiceRegisterInfo<UserServiceImpl> serviceRegisterInfo = new ServiceRegisterInfo<>(UserService.class.getName(), UserServiceImpl.class);
        serviceRegisterInfos.add(serviceRegisterInfo);
        // Initialize the PoYang application
        ProviderBootStrap.init(serviceRegisterInfos);

    }
}
