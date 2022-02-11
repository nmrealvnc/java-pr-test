package com.realvnc.gateway.repository;

import com.realvnc.gateway.model.User;

import java.util.Optional;

public interface UserRepository {
    Optional<User> findUserByToken(String token);
}
