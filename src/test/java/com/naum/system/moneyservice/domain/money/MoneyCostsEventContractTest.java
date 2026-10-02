package com.naum.system.moneyservice.domain.money;

import com.naum.system.contract.MoneyCostsEvent;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Контракт сообщения со стороны консьюмера. Тот же JSON проверяется в money-producer
 * (MoneyCostsKafkaContractTest) — если формат разъедется, упадёт один из тестов (см. задачу 24).
 */
class MoneyCostsEventContractTest {

    static final String CONTRACT_JSON = """
            {"schemaVersion":2,"eventId":"6f1c2c1e-6c55-4c4b-9a1e-2f4f0b8f6a01","occurredAt":"2024-05-01T10:15:42Z","userEmail":"ivan@test.com","expenses":1500,"category":"TAXI"}
            """;

    @Test
    void consumerDeserializesContractJson() {
        try (JsonDeserializer<MoneyCostsEvent> deserializer = new JsonDeserializer<>(MoneyCostsEvent.class, false)) {
            MoneyCostsEvent message = deserializer.deserialize("money_service",
                    CONTRACT_JSON.getBytes(StandardCharsets.UTF_8));

            assertThat(message.schemaVersion()).isEqualTo(2);
            assertThat(message.eventId()).isEqualTo(UUID.fromString("6f1c2c1e-6c55-4c4b-9a1e-2f4f0b8f6a01"));
            assertThat(message.category()).isEqualTo(MoneyCostsCategory.TAXI.name());
            assertThat(message.expenses()).isEqualTo(1500L);
            assertThat(message.occurredAt()).isEqualTo(Instant.parse("2024-05-01T10:15:42Z"));
            assertThat(message.userEmail()).isEqualTo("ivan@test.com");
        }
    }
}
