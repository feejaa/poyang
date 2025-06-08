package org.feejaa.service;

import org.feejaa.poyang.bootstrap.ProviderBootStrap;
import org.feejaa.poyang.model.ServiceRegisterInfo;
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
