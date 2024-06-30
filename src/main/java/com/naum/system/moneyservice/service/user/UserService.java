package com.naum.system.moneyservice.service.user;

import com.naum.system.moneyservice.domain.user.User;
import com.naum.system.moneyservice.domain.user.UserCreateDto;
import com.naum.system.moneyservice.repository.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User create(UserCreateDto userCreateDto) {
        if (!Pattern.compile("^(.+)@(.+)$")
                .matcher(userCreateDto.email())
                .matches()) {
            throw new IllegalArgumentException("User email is not valid");
        }
        User user = new User();
        user.setName(userCreateDto.name());
        user.setEmail(userCreateDto.email());
        return userRepository.save(user);
    }

    public @Nullable User findUserById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("User id is null");
        }
        return userRepository.findUserById(id);
    }

    public @Nullable User findUserByEmail(String email) {
        if (email == null) {
            throw new IllegalArgumentException("User email is null");
        }
        return userRepository.findUserByEmail(email);
    }

    public @NonNull ArrayList<User> findAllUser() {
        Iterable<User> all = userRepository.findAll();
        return StreamSupport.stream(all.spliterator(), false)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public @NonNull boolean deleteUserById(Long id) {
        if (id == null) {
            return false;
        }
        userRepository.deleteById(id);
        return true;
    }
}
