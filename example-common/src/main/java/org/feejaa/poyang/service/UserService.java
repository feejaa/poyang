package org.feejaa.poyang.service;

import org.feejaa.poyang.model.User;

public interface UserService {

    User getUser();

    default int getUserCount() {
        return 1;
    }
}
