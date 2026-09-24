package com.naum.system.moneyservice.repository.user;

import com.naum.system.moneyservice.AbstractIntegrationTest;
import com.naum.system.moneyservice.domain.money.MoneyCosts;
import com.naum.system.moneyservice.domain.money.MoneyCostsCategory;
import com.naum.system.moneyservice.domain.user.User;
import com.naum.system.moneyservice.repository.money.MoneyCostsRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
class UserRepositoryIntegrationTest extends AbstractIntegrationTest {

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
