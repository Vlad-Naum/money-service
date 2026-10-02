package com.naum.system.producer.domain;

import com.naum.system.contract.MoneyCostsEvent;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Контракт сообщения со стороны продюсера. Ожидаемый JSON совпадает с тем, что проверяет
 * MoneyCostsKafkaContractTest в money-service (см. задачу 24).
 */
class MoneyCostsEventContractTest {

    private static final String CONTRACT_JSON = """
            {"schemaVersion":2,"eventId":"6f1c2c1e-6c55-4c4b-9a1e-2f4f0b8f6a01","occurredAt":"2024-05-01T10:15:42Z","userEmail":"ivan@test.com","expenses":1500,"category":"TAXI"}
            """;

    @Test
    void producerSerializesMessageAccordingToContract() throws Exception {
        var event = new MoneyCostsEvent(
                MoneyCostsEvent.CURRENT_VERSION,
                UUID.fromString("6f1c2c1e-6c55-4c4b-9a1e-2f4f0b8f6a01"),
                Instant.parse("2024-05-01T10:15:42Z"),
                "ivan@test.com",
                1500L,
                MoneyCostsCategory.TAXI.name()
        );

        byte[] bytes;
        try (JsonSerializer<MoneyCostsEvent> serializer = new JsonSerializer<>()) {
            bytes = serializer.serialize("money_service", event);
        }

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode actual = objectMapper.readTree(bytes);
        JsonNode expected = objectMapper.readTree(CONTRACT_JSON);
        assertThat(actual).isEqualTo(expected);
    }
}
