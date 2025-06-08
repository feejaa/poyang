package org.feejaa;

import lombok.extern.slf4j.Slf4j;
import org.feejaa.base.EnablePoYang;
import org.feejaa.poyang.proxy.ServiceProxyFacotry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@EnablePoYang
@Slf4j
public class ConsumerApplication {

    public static void main(String[] args) throws InterruptedException {

        ConfigurableApplicationContext context = SpringApplication.run(ConsumerApplication.class, args);

        Thread.sleep(1000);

        TestClass bean = context.getBean(TestClass.class);
        bean.test();

        int i = 0;
    }
}
