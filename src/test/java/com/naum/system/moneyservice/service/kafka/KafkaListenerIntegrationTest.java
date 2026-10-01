package com.naum.system.moneyservice.service.kafka;

import com.naum.system.moneyservice.AbstractIntegrationTest;
import com.naum.system.moneyservice.domain.money.MoneyCosts;
import com.naum.system.moneyservice.domain.money.MoneyCostsCategory;
import com.naum.system.moneyservice.domain.user.User;
import com.naum.system.moneyservice.service.money.MoneyCostsService;
import com.naum.system.moneyservice.service.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

/**
 * Сквозной тест: сообщение в Kafka -> листенер -> запись в PostgreSQL.
 * Сообщения отправляются сырым JSON-ом, чтобы зафиксировать именно формат контракта, а не Java-класс.
 */
class KafkaListenerIntegrationTest extends AbstractIntegrationTest {

    private static final Duration TIMEOUT = Duration.ofSeconds(30);

    @Autowired
    private KafkaListenerEndpointRegistry listenerRegistry;

    @Autowired
    private UserService userService;

    @Autowired
    private MoneyCostsService moneyCostsService;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @BeforeEach
    void setUp() {
        // Пока у консьюмера auto.offset.reset=latest, сообщение, отправленное до назначения партиций,
        // будет пропущено. После задачи 22 (earliest) это ожидание можно убрать.
        await().atMost(TIMEOUT).until(() -> listenerRegistry.getListenerContainers().stream()
                .allMatch(container -> container.getAssignedPartitions() != null
                        && !container.getAssignedPartitions().isEmpty()));
    }

    @Test
    void newEmail_createsUserAndMoneyCosts() throws Exception {
        String email = uniqueEmail();

        send(json(MoneyCostsCategory.TAXI.name(), 1500, "2024-05-01 10:15", email));

        User user = awaitUser(email);
        MoneyCosts cost = awaitMoneyCosts(user, 1).get(0);
        assertThat(user.getName()).isEmpty();
        assertThat(cost.getMoneyCostsCategory()).isEqualTo(MoneyCostsCategory.TAXI);
        assertThat(cost.getExpenses()).isEqualTo(1500L);
        assertThat(cost.getDateTime()).isEqualTo(LocalDateTime.of(2024, 5, 1, 10, 15));
    }

    @Test
    void existingUser_isReusedAndKeepsName() throws Exception {
        String email = uniqueEmail();
        User existing = userService.create("Ivan", email);

        send(json(MoneyCostsCategory.RESTAURANTS.name(), 700, "2024-05-01 12:00", email));

        awaitMoneyCosts(existing, 1);
        assertThat(usersWithEmail(email)).singleElement()
                .satisfies(user -> assertThat(user.getName()).isEqualTo("Ivan"));
    }

    @Test
    void emailInDifferentCase_isMatchedToExistingUser() throws Exception {
        String email = uniqueEmail();
        User existing = userService.create("Ivan", email);

        send(json(MoneyCostsCategory.TAXI.name(), 300, "2024-05-01 13:00", email.toUpperCase(Locale.ROOT)));

        awaitMoneyCosts(existing, 1);
        assertThat(usersWithEmail(email)).hasSize(1);
    }

    @Test
    void unknownCategoryId_isSavedWithDefaultCategory() throws Exception {
        String email = uniqueEmail();

        send(json("UNKNOWN_CATEGORY", 50, "2024-05-01 09:00", email));

        User user = awaitUser(email);
        assertThat(awaitMoneyCosts(user, 1).get(0).getMoneyCostsCategory()).isEqualTo(MoneyCostsCategory.OTHER);
    }

    @Test
    void invalidEmail_isNotSaved_andNextMessageIsProcessed() throws Exception {
        String invalidEmail = "not-an-email-" + UUID.randomUUID();
        String validEmail = uniqueEmail();

        send(json(MoneyCostsCategory.TAXI.name(), 100, "2024-05-01 10:00", invalidEmail));
        send(json(MoneyCostsCategory.TAXI.name(), 200, "2024-05-01 11:00", validEmail));

        // Сообщения одной партиции обрабатываются по порядку: раз обработано второе,
        // первое уже отброшено (сейчас — после повторов DefaultErrorHandler, после задачи 17 — сразу в DLT).
        User user = awaitUser(validEmail);
        awaitMoneyCosts(user, 1);
        assertThat(userService.findByEmail(invalidEmail)).isEmpty();
    }

    @Disabled("Задача 18: консьюмер не идемпотентен — повторная доставка события создаёт дубль")
    @Test
    void sameEventDeliveredTwice_isSavedOnce() throws Exception {
        String email = uniqueEmail();
        String markerEmail = uniqueEmail();
        String message = """
                {"eventId":"%s","moneyCostsCategoryId":2,"expenses":100,"localDateTime":"2024-05-01 10:00","userEmail":"%s"}
                """.formatted(UUID.randomUUID(), email);

        send(message);
        send(message);
        // Маркер вместо sleep: при одной партиции он обработается только после обоих дублей.
        send(json(MoneyCostsCategory.TAXI.name(), 1, "2024-05-01 10:00", markerEmail));

        awaitMoneyCosts(awaitUser(markerEmail), 1);
        User user = awaitUser(email);
        assertThat(moneyCostsService.findAllByUserId(user.getId())).hasSize(1);
    }

    private User awaitUser(String email) {
        return await().atMost(TIMEOUT)
                .until(() -> userService.findByEmail(email), Optional::isPresent)
                .orElseThrow();
    }

    private List<MoneyCosts> awaitMoneyCosts(User user, int expectedCount) {
        return await().atMost(TIMEOUT)
                .until(() -> moneyCostsService.findAllByUserId(user.getId()), costs -> costs.size() == expectedCount);
    }

    private List<User> usersWithEmail(String email) {
        return userService.findAllUser().stream()
                .filter(user -> email.equalsIgnoreCase(user.getEmail()))
                .toList();
    }

    private void send(String json) throws Exception {
        kafkaTemplate.send(TOPIC, json).get(10, TimeUnit.SECONDS);
    }

    private static String json(String category, long expenses, String dateTime, String email) {
        return """
                {"moneyCostsCategory":"%s","expenses":%d,"localDateTime":"%s","userEmail":"%s"}
                """.formatted(category, expenses, dateTime, email);
    }

    private static String uniqueEmail() {
        return UUID.randomUUID() + "@test.com";
    }
}