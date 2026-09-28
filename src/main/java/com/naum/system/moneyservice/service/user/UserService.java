package com.naum.system.moneyservice.service.user;

import com.naum.system.moneyservice.domain.user.User;
import com.naum.system.moneyservice.repository.user.UserRepository;
import com.naum.system.moneyservice.service.exception.InvalidEmailException;
import com.naum.system.moneyservice.service.exception.UserAlreadyExistsException;
import com.naum.system.moneyservice.service.exception.UserNotFoundException;
import com.naum.system.moneyservice.validation.EmailRules;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User create(String name, String email) {
        if (!EmailRules.isValid(email)) {
            throw new InvalidEmailException();
        }
        email = EmailRules.normalize(email);
        if (userRepository.existsByEmail(email)) {
            throw new UserAlreadyExistsException();
        }
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        try {
            return userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new UserAlreadyExistsException();
        }
    }

    public User getOrCreate(String email) {
        return userRepository.findUserByEmail(email).orElseGet(() -> {
            try {
                return create("", email);
            } catch (UserAlreadyExistsException e) {
                // Кто-то создал пользователя между проверкой и вставкой — берём его
                return userRepository.findUserByEmail(email).orElseThrow();
            }
        });
    }

    public @NonNull User getUserById(@NonNull Long id) {
        User user = userRepository.findUserById(id);
        if (user == null) {
            throw UserNotFoundException.userNotFoundByIdException(id);
        }
        return user;
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findUserByEmail(EmailRules.normalize(email));
    }

    public @NonNull ArrayList<User> findAllUser() {
        Iterable<User> all = userRepository.findAll();
        return StreamSupport.stream(all.spliterator(), false)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public void deleteUserById(@NonNull Long id) {
        userRepository.deleteById(id);
    }
}
