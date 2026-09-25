package com.naum.system.moneyservice;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.utility.DockerImageName;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Базовый класс интеграционных тестов: поднимает PostgreSQL и Kafka в Docker один раз на весь прогон.
 * <p>
 * Контейнеры запускаются в static-блоке (singleton container pattern), а не через {@code @Container}:
 * с {@code @Container} в базовом классе контейнеры перезапускаются для каждого тестового класса,
 * а закешированный Spring-контекст продолжает смотреть на старые порты.
 * <p>
 * Используется {@code @SpringBootTest}, а не {@code @DataJpaTest}: {@code @EnableWebMvc} на главном классе
 * требует ServletContext, которого нет в не-веб слайсах (см. задачу 2).
 * <p>
 * Адреса передаются через {@code @DynamicPropertySource}, а не {@code @ServiceConnection}: KafkaConfig читает
 * {@code spring.kafka.bootstrap-servers} напрямую через {@code @Value}, и ServiceConnection до него не дойдёт
 * (см. задачу 23 — после неё можно перейти на {@code @ServiceConnection}).
 */
@SpringBootTest
public abstract class AbstractIntegrationTest {

    public static final String TOPIC = "money_service";

    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>(DockerImageName.parse("postgres:14"));

    static final KafkaContainer KAFKA = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.2.1"));

    static {
        Startables.deepStart(POSTGRES, KAFKA).join();
        createTopic();
    }

    @DynamicPropertySource
    static void containerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.kafka.bootstrap-servers", KAFKA::getBootstrapServers);
    }

    protected static String kafkaBootstrapServers() {
        return KAFKA.getBootstrapServers();
    }

    /**
     * Топик создаём явно: приложение полагается на авто-создание топиков брокером (см. задачу 21).
     */
    private static void createTopic() {
        Map<String, Object> config = Map.of(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA.getBootstrapServers());
        try (AdminClient admin = AdminClient.create(config)) {
            admin.createTopics(List.of(new NewTopic(TOPIC, 1, (short) 1)))
                    .all()
                    .get(30, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new IllegalStateException("Не удалось создать топик " + TOPIC, e);
        }
    }
}
