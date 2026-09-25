package com.naum.system.moneyservice.repository.user;

import com.naum.system.moneyservice.domain.user.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    User findUserById(Long id);

    Optional<User> findUserByEmail(String email);
}
