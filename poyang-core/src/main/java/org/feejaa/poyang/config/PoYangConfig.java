package org.feejaa.poyang.config;

import lombok.Data;
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

        private String serverHost = "localhost";

        private Integer port = 8080;

        private boolean isMock = false;

        private String serializer = SerializerKeys.JDK;
    }
}
