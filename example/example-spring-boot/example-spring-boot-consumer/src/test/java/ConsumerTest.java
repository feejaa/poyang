import org.feejaa.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class ConsumerTest {

    @Resource
    private UserService userService;

    @Test
    void test1() {
        userService.getUser();
    }
}