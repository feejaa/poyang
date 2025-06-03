package org.feejaa.poyang.config;

import lombok.Data;
import org.feejaa.poyang.loadBalance.LoadBalancerKey;
import org.feejaa.poyang.serializer.SerializerKeys;

/**
 * RPC frameWork config
 */
@Data
public class PoYangConfig {

    public PoYang poyang;

    @Data
    public static class PoYang{

        private String name = "poyang-rpc";

        private String version = "1.0.0";

        private String serverHost = "127.0.0.1";

        private Integer port = 8080;

        private boolean isMock = false;

        private String serializer = SerializerKeys.JDK;

        private String loadBalancer = LoadBalancerKey.RANDOM;
    }

    /**
     * 注册中心配置
     */

    private RegistryConfig registryConfig = new RegistryConfig();


}
