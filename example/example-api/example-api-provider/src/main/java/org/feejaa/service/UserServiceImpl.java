package org.feejaa.service;

import org.feejaa.poyang.model.User;
import org.feejaa.poyang.service.UserService;

public class UserServiceImpl implements UserService {
    @Override
    public User getUser() {
        return User.builder().name("test------用户").build();
    }
}
