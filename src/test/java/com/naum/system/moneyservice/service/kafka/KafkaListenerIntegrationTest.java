package com.naum.system.moneyservice.service.kafka;

import com.naum.system.moneyservice.AbstractIntegrationTest;
import com.naum.system.moneyservice.domain.money.MoneyCosts;
import com.naum.system.moneyservice.domain.money.MoneyCostsCategory;
import com.naum.system.moneyservice.domain.user.User;
import com.naum.system.moneyservice.domain.user.UserCreateDto;
import com.naum.system.moneyservice.service.money.MoneyCostsService;
import com.naum.system.moneyservice.service.user.UserService;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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

    private DefaultKafkaProducerFactory<String, String> producerFactory;
    private KafkaTemplate<String, String> kafkaTemplate;

    @BeforeEach
    void setUp() {
        Map<String, Object> config = Map.of(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServers(),
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerFactory = new DefaultKafkaProducerFactory<>(config);
        kafkaTemplate = new KafkaTemplate<>(producerFactory);

        // У новой consumer group по умолчанию auto.offset.reset=latest: сообщение, отправленное
        // до назначения партиций, будет пропущено. Поэтому ждём, пока листенер получит партиции (см. задачу 22).
        await().atMost(TIMEOUT).until(() -> listenerRegistry.getListenerContainers().stream()
                .allMatch(container -> container.getAssignedPartitions() != null
                        && !container.getAssignedPartitions().isEmpty()));
    }

    @AfterEach
    void tearDown() {
        producerFactory.destroy();
    }

    @Test
    void message_forUnknownEmail_createsUserAndMoneyCosts() throws Exception {
        String email = uniqueEmail();

        send(json(2, 1500, "2024-05-01 10:15", email));

        User user = await().atMost(TIMEOUT).until(() -> userService.findUserByEmail(email), Objects::nonNull);
        List<MoneyCosts> costs = awaitMoneyCosts(user, 1);

        assertThat(user.getName()).isEmpty();
        MoneyCosts cost = costs.get(0);
        assertThat(cost.getMoneyCostsCategory()).isEqualTo(MoneyCostsCategory.TAXI);
        assertThat(cost.getExpenses()).isEqualTo(1500L);
        assertThat(cost.getDateTime()).isEqualTo(LocalDateTime.of(2024, 5, 1, 10, 15));
    }

    @Test
    void message_forExistingUser_reusesUser() throws Exception {
        String email = uniqueEmail();
        User existing = userService.create(new UserCreateDto("Ivan", email));

        send(json(5, 700, "2024-05-01 12:00", email));

        awaitMoneyCosts(existing, 1);
        List<User> usersWithEmail = userService.findAllUser().stream()
                .filter(user -> email.equals(user.getEmail()))
                .toList();
        assertThat(usersWithEmail).hasSize(1);
        assertThat(usersWithEmail.get(0).getName()).isEqualTo("Ivan");
    }

    @Test
    void message_withUnknownCategoryId_isSavedWithDefaultCategory() throws Exception {
        String email = uniqueEmail();

        send(json(100, 50, "2024-05-01 09:00", email));

        User user = await().atMost(TIMEOUT).until(() -> userService.findUserByEmail(email), Objects::nonNull);
        assertThat(awaitMoneyCosts(user, 1).get(0).getMoneyCostsCategory()).isEqualTo(MoneyCostsCategory.OTHER);
    }

    @Disabled("Задача 18: консьюмер не идемпотентен — повторная доставка события создаёт дубль")
    @Test
    void sameEventDeliveredTwice_isSavedOnce() throws Exception {
        String email = uniqueEmail();
        String eventId = UUID.randomUUID().toString();
        String message = """
                {"eventId":"%s","moneyCostsCategoryId":2,"expenses":100,"localDateTime":"2024-05-01 10:00","userEmail":"%s"}
                """.formatted(eventId, email);

        send(message);
        send(message);

        User user = await().atMost(TIMEOUT).until(() -> userService.findUserByEmail(email), Objects::nonNull);
        awaitMoneyCosts(user, 1);
        // Даём второму сообщению время обработаться и проверяем, что запись осталась одна.
        TimeUnit.SECONDS.sleep(3);
        assertThat(moneyCostsService.findAllByUserId(user.getId())).hasSize(1);
    }

    private List<MoneyCosts> awaitMoneyCosts(User user, int expectedCount) {
        return await().atMost(TIMEOUT)
                .until(() -> moneyCostsService.findAllByUserId(user.getId()), costs -> costs.size() == expectedCount);
    }

    private void send(String json) throws Exception {
        kafkaTemplate.send(TOPIC, json).get(10, TimeUnit.SECONDS);
    }

    private static String json(int categoryId, long expenses, String dateTime, String email) {
        return """
                {"moneyCostsCategoryId":%d,"expenses":%d,"localDateTime":"%s","userEmail":"%s"}
                """.formatted(categoryId, expenses, dateTime, email);
    }

    private static String uniqueEmail() {
        return UUID.randomUUID() + "@test.com";
    }
}
