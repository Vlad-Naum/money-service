package com.naum.system.producer.domain;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Контракт сообщения со стороны продюсера. Ожидаемый JSON совпадает с тем, что проверяет
 * MoneyCostsKafkaContractTest в money-service (см. задачу 24).
 */
class MoneyCostsKafkaContractTest {

    private static final String CONTRACT_JSON = """
            {"moneyCostsCategoryId":2,"expenses":1500,"localDateTime":"2024-05-01 10:15","userEmail":"ivan@test.com"}
            """;

    @Test
    void producerSerializesMessageAccordingToContract() throws Exception {
        MoneyCostsKafka message = MoneyCostsKafka.builder()
                .moneyCostsCategoryId(2)
                .expenses(1500)
                // Секунды будут потеряны: формат даты в контракте — "yyyy-MM-dd HH:mm"
                .localDateTime(LocalDateTime.of(2024, 5, 1, 10, 15, 42))
                .userEmail("ivan@test.com")
                .build();

        byte[] bytes;
        try (JsonSerializer<MoneyCostsKafka> serializer = new JsonSerializer<>()) {
            bytes = serializer.serialize("money_service", message);
        }

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode actual = objectMapper.readTree(bytes);
        JsonNode expected = objectMapper.readTree(CONTRACT_JSON);
        assertThat(actual).isEqualTo(expected);
    }
}
