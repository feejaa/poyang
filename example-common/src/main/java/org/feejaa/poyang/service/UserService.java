package org.feejaa.poyang.service;

import org.feejaa.poyang.model.User;

public interface UserService {

    User getUser(User user);

    void setUser(User user);

    default int getUserCount() {
        return 1;
    }
}
