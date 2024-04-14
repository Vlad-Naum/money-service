package com.naum.system.moneyservice.repository;

import com.naum.system.moneyservice.domain.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    User findUserById(Long id);

    User findUserByUserNameAndPassword(String userName, String password);
}
