package org.feejaa.poyang;

import lombok.extern.slf4j.Slf4j;
import org.feejaa.poyang.config.PoYangConfig;
import org.feejaa.poyang.constant.RpcConstant;
import org.feejaa.poyang.registry.Registry;
import org.feejaa.poyang.registry.RegistryFactory;
import org.feejaa.poyang.utils.ConfigUtils;

@Slf4j
public class PoYangApplication {

    private static volatile PoYangConfig poYangConfig;

    public static void init(PoYangConfig newPoYangConfig) {
        poYangConfig = newPoYangConfig;
        log.info("init poyang rpc framework success={}", poYangConfig);

        Registry registry = RegistryFactory
                .getRegistry(poYangConfig.getRegistryConfig().getRegistryType());
        registry.init(poYangConfig.getRegistryConfig());

        log.info("register init success = {}", registry);

        Runtime.getRuntime().addShutdownHook(new Thread(registry::destroy));
    }


    public static void init() {
        PoYangConfig tempConfig;
        try {
            tempConfig = ConfigUtils.loadConfig(PoYangConfig.class, RpcConstant.DEFAULT_CONFIG_PREIFIX);
        } catch (Exception e) {
            log.error("init poyang rpc framework error", e);
            tempConfig = new PoYangConfig();
        }
        init(tempConfig);
    }

    public static PoYangConfig getRpcConfig() {
        if (poYangConfig == null) {
            synchronized (PoYangApplication.class) {
                if (poYangConfig == null) {
                    init();
                }
            }
        }
        return poYangConfig;
    }
}
