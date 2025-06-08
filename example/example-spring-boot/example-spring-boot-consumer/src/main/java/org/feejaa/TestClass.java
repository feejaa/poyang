package org.feejaa;

import lombok.extern.slf4j.Slf4j;
import org.feejaa.base.PoYangInflow;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TestClass {

    @PoYangInflow
    private UserService userService;

    public void test() {
        User user = userService.getUser();
        log.info("获取到的用户信息: {}", user);
    }

}
