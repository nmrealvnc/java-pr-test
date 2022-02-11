package com.realvnc.gateway.repository;

import com.realvnc.gateway.model.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepository {

    @Override
    public Optional<User> findUserByToken(String token) {
        return Optional.empty(); // TODO
    }

}
