package org.feejaa;

import lombok.extern.slf4j.Slf4j;
import org.feejaa.poyang.bootstrap.ConsumerBootstrap;
import org.feejaa.poyang.model.User;
import org.feejaa.poyang.proxy.ServiceProxyFacotry;
import org.feejaa.poyang.service.UserService;

@Slf4j
public class ConsumerExample {

    public static void main(String[] args) {

        ConsumerBootstrap.init();

        UserService userService = ServiceProxyFacotry.getProxy(UserService.class);
        User user = userService.getUser();
        log.info("user:{}", user);
        // 获得代理信息，发送 http 或者其他方式建连接



    }
}
