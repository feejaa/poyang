package org.feejaa.poyang.example;

import lombok.extern.slf4j.Slf4j;
import org.feejaa.poyang.model.User;
import org.feejaa.poyang.proxy.ServiceProxyFacotry;
import org.feejaa.poyang.service.UserService;

@Slf4j
public class ConsumerExample {

    public static void main(String[] args) {

        UserService userService = ServiceProxyFacotry.getProxy(UserService.class);
        User user = new User();
        user.setName("poyang");
        // 调用
        User newUser = userService.getUser(user);
        if (newUser != null) {
            log.info("newUser:{}", newUser);
        } else {
            log.info("newUser is null");
        }
        Integer userCount = userService.getUserCount();
        log.info("userCount:{}", userCount);
    }
}
