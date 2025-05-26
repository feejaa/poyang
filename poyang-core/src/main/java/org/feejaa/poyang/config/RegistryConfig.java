package org.feejaa.poyang.config;

import lombok.Data;

@Data
public class RegistryConfig {

    /**
     * 注册中心类别
     */
    private String registry = "etcd";

    private String address = "http://localhost:2380";

    private String username;

    private String password;

    private Long timeout = 1000L;
}
