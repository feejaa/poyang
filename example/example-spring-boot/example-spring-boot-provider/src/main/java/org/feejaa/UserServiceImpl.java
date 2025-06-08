package org.feejaa;


import org.feejaa.base.PoYangOutflow;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@PoYangOutflow
@Component
public class UserServiceImpl implements UserService {

    @Override
    public User getUser() {
        return User.builder().name("spring-boot-test------用户").build();
    }

}
