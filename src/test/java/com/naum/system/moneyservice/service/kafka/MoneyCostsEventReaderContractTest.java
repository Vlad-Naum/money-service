package com.naum.system.moneyservice.service.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.naum.system.moneyservice.domain.money.MoneyCostsCategory;
import com.naum.system.moneyservice.service.exception.UnsupportedEventException;
import com.naum.system.moneyservice.service.kafka.message.RegisterExpenseCommand;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.context.annotation.Import;

import com.naum.system.contract.ContractSamples;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Контракт сообщения со стороны консьюмера: образец из money-contract разбирается тем же кодом
 * и тем же ObjectMapper, что и в листенере. Образец общий с контрактным тестом продюсера.
 */
@JsonTest
@Import(MoneyCostsEventReader.class)
class MoneyCostsEventReaderContractTest {

    @Autowired
    private MoneyCostsEventReader reader;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void readsContractSample() throws Exception {
        RegisterExpenseCommand command = reader.read(ContractSamples.moneyCostsV2());

        assertThat(command.eventId()).isEqualTo(UUID.fromString("6f1c2c1e-6c55-4c4b-9a1e-2f4f0b8f6a01"));
        assertThat(command.occurredAt()).isEqualTo(Instant.parse("2024-05-01T10:15:42Z"));
        assertThat(command.userEmail()).isEqualTo("ivan@test.com");
        assertThat(command.expenses()).isEqualTo(1500L);
        assertThat(command.category()).isEqualTo(MoneyCostsCategory.TAXI);
    }

    @Test
    void ignoresUnknownFields() throws Exception {
        ObjectNode json = sample();
        json.put("note", "field from a newer producer");

        assertThat(reader.read(json.toString()).category()).isEqualTo(MoneyCostsCategory.TAXI);
    }

    @Test
    void unknownCategory_becomesOther() throws Exception {
        ObjectNode json = sample();
        json.put("category", "SPACE_TRAVEL");

        assertThat(reader.read(json.toString()).category()).isEqualTo(MoneyCostsCategory.OTHER);
    }

    @Test
    void rejectsUnsupportedSchemaVersion() throws Exception {
        ObjectNode json = sample();
        json.put("schemaVersion", 3);

        assertThatThrownBy(() -> reader.read(json.toString()))
                .isInstanceOf(UnsupportedEventException.class);
    }

    @Test
    void rejectsOldFormatWithoutSchemaVersion() {
        String v1 = """
                {"moneyCostsCategory":"TAXI","expenses":1500,"localDateTime":"2024-05-01 10:15","userEmail":"ivan@test.com"}
                """;

        assertThatThrownBy(() -> reader.read(v1))
                .isInstanceOf(UnsupportedEventException.class);
    }

    @Test
    void rejectsMessageWithoutEventId() throws Exception {
        ObjectNode json = sample();
        json.remove("eventId");

        assertThatThrownBy(() -> reader.read(json.toString()))
                .isInstanceOf(UnsupportedEventException.class);
    }

    @Test
    void malformedJson_throwsJsonProcessingException() {
        assertThatThrownBy(() -> reader.read("not a json"))
                .isInstanceOf(JsonProcessingException.class);
    }

    private ObjectNode sample() throws JsonProcessingException {
        return (ObjectNode) objectMapper.readTree(ContractSamples.moneyCostsV2());
    }
}
