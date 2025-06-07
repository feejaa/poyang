package org.feejaa.boot;

import lombok.extern.slf4j.Slf4j;
import org.feejaa.base.EnablePoYang;
import org.feejaa.poyang.PoYangApplication;
import org.feejaa.poyang.config.PoYangConfig;
import org.feejaa.poyang.server.tcp.TcpServer;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;

@Slf4j
public class PoYangInitRegistart implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(org.springframework.core.type.AnnotationMetadata importingClassMetadata, org.springframework.beans.factory.support.BeanDefinitionRegistry registry) {
        boolean enable = (boolean) importingClassMetadata.getAnnotationAttributes
                (EnablePoYang.class.getName()).get("value");
        if (!enable) {
            log.info("PoYang is not enabled, skipping initialization.");
            return;
        }
        log.info("Initializing PoYang...");
        PoYangApplication.init();

        final PoYangConfig.PoYang poyang = PoYangApplication.getRpcConfig().getPoyang();
        TcpServer server = new TcpServer();
        server.doStart(poyang.getPort());
        log.info("PoYang server started on port: {}", poyang.getPort());
    }
}
