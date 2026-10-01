package com.naum.system.moneyservice;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.utility.DockerImageName;

/**
 * Базовый класс интеграционных тестов: поднимает PostgreSQL и Kafka в Docker один раз на весь прогон.
 * <p>
 * Контейнеры запускаются в static-блоке (singleton container pattern), а не через {@code @Container}:
 * с {@code @Container} в базовом классе контейнеры перезапускаются для каждого тестового класса,
 * а закешированный Spring-контекст продолжает смотреть на старые порты.
 */
@SpringBootTest
public abstract class AbstractIntegrationTest {

    public static final String TOPIC = "money_service";

    @ServiceConnection
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>(DockerImageName.parse("postgres:14"));

    @ServiceConnection
    static final KafkaContainer KAFKA = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.2.1"));

    static {
        Startables.deepStart(POSTGRES, KAFKA).join();
    }
}
