package com.naum.system.moneyservice.repository.money;

import com.naum.system.moneyservice.AbstractIntegrationTest;
import com.naum.system.moneyservice.domain.money.MoneyCosts;
import com.naum.system.moneyservice.domain.money.MoneyCostsCategory;
import com.naum.system.moneyservice.domain.user.User;
import com.naum.system.moneyservice.repository.user.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Проверяет native-запросы репозитория на настоящем PostgreSQL.
 * Каждый тест выполняется в транзакции и откатывается после завершения.
 */
@Transactional
class MoneyCostsRepositoryIntegrationTest extends AbstractIntegrationTest {

    private static final LocalDate DAY = LocalDate.of(2024, 5, 1);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MoneyCostsRepository moneyCostsRepository;

    @PersistenceContext
    private EntityManager entityManager;

    private User user;

    @BeforeEach
    void setUp() {
        user = saveUser();
        User anotherUser = saveUser();

        // Нужный день, включая границы суток
        save(user, DAY.atStartOfDay(), MoneyCostsCategory.TAXI, 100L);
        save(user, DAY.atTime(12, 0), MoneyCostsCategory.AUTO, 200L);
        save(user, DAY.atTime(23, 59, 59), MoneyCostsCategory.TAXI, 300L);
        // Соседние дни — не должны попадать в выборку
        save(user, DAY.minusDays(1).atTime(23, 59, 59), MoneyCostsCategory.TAXI, 400L);
        save(user, DAY.plusDays(1).atStartOfDay(), MoneyCostsCategory.TAXI, 500L);
        // Другой пользователь в тот же день
        save(anotherUser, DAY.atTime(12, 0), MoneyCostsCategory.TAXI, 600L);

        entityManager.flush();
    }

    @Test
    void findByDateAndUserId_returnsOnlyCostsOfUserForThatDay() {
        Page<MoneyCosts> page = moneyCostsRepository.findByDateAndUserId(DAY, user.getId(),
                PageRequest.of(0, 10, Sort.by("id")));

        assertThat(page.getContent())
                .extracting(MoneyCosts::getExpenses)
                .containsExactly(100L, 200L, 300L);
    }

    @Test
    void findByDateAndUserId_supportsPagination() {
        Page<MoneyCosts> firstPage = moneyCostsRepository.findByDateAndUserId(DAY, user.getId(),
                PageRequest.of(0, 2, Sort.by("id")));
        Page<MoneyCosts> secondPage = moneyCostsRepository.findByDateAndUserId(DAY, user.getId(),
                PageRequest.of(1, 2, Sort.by("id")));

        assertThat(firstPage.getTotalElements()).isEqualTo(3);
        assertThat(firstPage.getTotalPages()).isEqualTo(2);
        assertThat(firstPage.getContent()).extracting(MoneyCosts::getExpenses).containsExactly(100L, 200L);
        assertThat(secondPage.getContent()).extracting(MoneyCosts::getExpenses).containsExactly(300L);
    }

    @Test
    void findByDateAndUserId_supportsDescendingSortById() {
        Page<MoneyCosts> page = moneyCostsRepository.findByDateAndUserId(DAY, user.getId(),
                PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "id")));

        assertThat(page.getContent())
                .extracting(MoneyCosts::getExpenses)
                .containsExactly(300L, 200L, 100L);
    }

    @Test
    void findByDateAndUserIdAndCategory_filtersByCategory() {
        Page<MoneyCosts> page = moneyCostsRepository.findByDateAndUserIdAndCategory(DAY, user.getId(),
                MoneyCostsCategory.TAXI, PageRequest.of(0, 10, Sort.by("id")));

        assertThat(page.getContent())
                .extracting(MoneyCosts::getExpenses)
                .containsExactly(100L, 300L);
    }

    @Test
    void findAllByUserId_returnsAllCostsOfUser() {
        assertThat(moneyCostsRepository.findAllByUserId(user.getId()))
                .extracting(MoneyCosts::getExpenses)
                .containsExactlyInAnyOrder(100L, 200L, 300L, 400L, 500L);
    }

    @Disabled("Задача 13: в native query сортировка подставляет имя поля сущности (dateTime), а не колонки (date_time)")
    @Test
    void findByDateAndUserId_supportsSortByDateTime() {
        Page<MoneyCosts> page = moneyCostsRepository.findByDateAndUserId(DAY, user.getId(),
                PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "date_time")));

        assertThat(page.getContent())
                .extracting(MoneyCosts::getExpenses)
                .containsExactly(300L, 200L, 100L);
    }

    private User saveUser() {
        User newUser = new User();
        newUser.setEmail(UUID.randomUUID() + "@test.com");
        return userRepository.save(newUser);
    }

    private void save(User owner, LocalDateTime dateTime, MoneyCostsCategory category, Long expenses) {
        MoneyCosts moneyCosts = new MoneyCosts();
        moneyCosts.setUser(owner);
        moneyCosts.setDateTime(dateTime);
        moneyCosts.setMoneyCostsCategory(category);
        moneyCosts.setExpenses(expenses);
        moneyCostsRepository.save(moneyCosts);
    }
}
