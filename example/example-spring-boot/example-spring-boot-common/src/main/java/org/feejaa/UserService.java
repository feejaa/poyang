package org.feejaa;


import org.springframework.stereotype.Service;


public interface UserService {

    User getUser();

    default int getUserCount() {
        return 1;
    }
}