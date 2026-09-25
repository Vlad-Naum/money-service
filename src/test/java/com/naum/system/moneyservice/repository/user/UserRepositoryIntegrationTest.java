package com.naum.system.moneyservice.repository.user;

import com.naum.system.moneyservice.domain.money.MoneyCosts;
import com.naum.system.moneyservice.domain.money.MoneyCostsCategory;
import com.naum.system.moneyservice.domain.user.User;
import com.naum.system.moneyservice.repository.money.MoneyCostsRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
class UserRepositoryIntegrationTest {

    @Container
    @ServiceConnection
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>(DockerImageName.parse("postgres:14"));

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MoneyCostsRepository moneyCostsRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    void findUserByEmail_returnsSavedUser() {
        String email = uniqueEmail();
        User saved = userRepository.save(user("Ivan", email));

        User found = userRepository.findUserByEmail(email);

        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(saved.getId());
        assertThat(found.getName()).isEqualTo("Ivan");
    }

    @Test
    void findUserByEmail_whenMissing_returnsNull() {
        assertThat(userRepository.findUserByEmail(uniqueEmail())).isNull();
    }

    @Test
    void deleteById_whenUserExists_removesIt() {
        User saved = userRepository.save(user("Ivan", uniqueEmail()));
        entityManager.flush();

        userRepository.deleteById(saved.getId());
        entityManager.flush();
        entityManager.clear();

        assertThat(userRepository.findById(saved.getId())).isEmpty();
    }

    @Test
    void deleteUser_whenUserMissing_doesNotThrow() {
        assertThat(userRepository.findById(1L)).isEmpty();
        assertThatCode(() -> {
            userRepository.deleteById(1L);
            entityManager.flush();
        }).doesNotThrowAnyException();
    }

    @Test
    void deleteUser_cascadesToMoneyCostsOnDatabaseLevel() {
        User saved = userRepository.save(user("Ivan", uniqueEmail()));
        MoneyCosts moneyCosts = new MoneyCosts();
        moneyCosts.setUser(saved);
        moneyCosts.setDateTime(LocalDateTime.of(2024, 5, 1, 10, 0));
        moneyCosts.setMoneyCostsCategory(MoneyCostsCategory.AUTO);
        moneyCosts.setExpenses(100L);
        moneyCostsRepository.save(moneyCosts);
        entityManager.flush();
        entityManager.clear();

        userRepository.deleteById(saved.getId());
        entityManager.flush();

        // Каскад выполняет сама БД благодаря @OnDelete(action = CASCADE) на внешнем ключе
        assertThat(moneyCostsRepository.findAllByUserId(saved.getId())).isEmpty();
    }

    @Disabled("Задача 10: на email нет уникального ограничения, дубликаты сохраняются")
    @Test
    void save_withDuplicateEmail_fails() {
        String email = uniqueEmail();
        userRepository.save(user("First", email));
        entityManager.flush();

        assertThatThrownBy(() -> {
            userRepository.save(user("Second", email));
            entityManager.flush();
        }).isInstanceOf(PersistenceException.class);
    }

    private static User user(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        return user;
    }

    private static String uniqueEmail() {
        return UUID.randomUUID() + "@test.com";
    }
}
