package com.naum.system.moneyservice.repository.user;

import com.naum.system.moneyservice.domain.user.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    User findUserById(Long id);

    Optional<User> findUserByEmail(String email);

    boolean existsByEmail(String email);

    @Modifying
    @Query(value = """
        INSERT INTO user_account (id, name, email)
        VALUES (nextval('user_account_seq'), '', :email)
        ON CONFLICT (email) DO NOTHING
        """, nativeQuery = true)
    void insertIfAbsent(@Param("email") String email);
}
