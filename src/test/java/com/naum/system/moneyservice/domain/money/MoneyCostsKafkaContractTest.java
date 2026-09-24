package com.naum.system.moneyservice.domain.money;

import org.junit.jupiter.api.Test;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Контракт сообщения со стороны консьюмера. Тот же JSON проверяется в money-producer
 * (MoneyCostsKafkaContractTest) — если формат разъедется, упадёт один из тестов (см. задачу 24).
 */
class MoneyCostsKafkaContractTest {

    static final String CONTRACT_JSON = """
            {"moneyCostsCategoryId":2,"expenses":1500,"localDateTime":"2024-05-01 10:15","userEmail":"ivan@test.com"}
            """;

    @Test
    void consumerDeserializesContractJson() {
        try (JsonDeserializer<MoneyCostsKafka> deserializer = new JsonDeserializer<>(MoneyCostsKafka.class, false)) {
            MoneyCostsKafka message = deserializer.deserialize("money_service",
                    CONTRACT_JSON.getBytes(StandardCharsets.UTF_8));

            assertThat(message.getMoneyCostsCategoryId()).isEqualTo(2);
            assertThat(message.getExpenses()).isEqualTo(1500L);
            assertThat(message.getLocalDateTime()).isEqualTo(LocalDateTime.of(2024, 5, 1, 10, 15));
            assertThat(message.getUserEmail()).isEqualTo("ivan@test.com");
        }
    }
}
