package org.feejaa.poyang.config;

import lombok.Data;

@Data
public class RegistryConfig {

    /**
     * 注册中心类别
     */
    private String registryType = "etcd";

    private String address = "http://localhost:2379";

    private String username;

    private String password;

    private Long timeout = 1000L;
}
