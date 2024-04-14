package com.naum.system.moneyservice.service;

import com.naum.system.moneyservice.domain.User;
import com.naum.system.moneyservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User create(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User is null");
        }
        return userRepository.save(user);
    }

    public @Nullable User findUserById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("User id is null");
        }
        return userRepository.findUserById(id);
    }

    public @Nullable User findUserByUserNameAndPassword(String userName, String password) {
        if (userName == null) {
            throw new IllegalArgumentException("User name is null");
        }
        if (password == null) {
            throw new IllegalArgumentException("User password is null");
        }
        return userRepository.findUserByUserNameAndPassword(userName, password);
    }
}
