package com.naum.system.moneyservice.service.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.naum.system.contract.MoneyCostsEvent;
import com.naum.system.moneyservice.domain.money.MoneyCostsCategory;
import com.naum.system.moneyservice.service.exception.UnsupportedEventException;
import com.naum.system.moneyservice.service.kafka.message.RegisterExpenseCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MoneyCostsEventReader {

    private final ObjectMapper objectMapper;

    public RegisterExpenseCommand read(String json) throws JsonProcessingException {
        MoneyCostsEvent event = objectMapper.readValue(json, MoneyCostsEvent.class);
        validate(event);
        return new RegisterExpenseCommand(
                event.eventId(),
                event.occurredAt(),
                event.userEmail(),
                event.expenses(),
                MoneyCostsCategory.getOrDefault(event.category()));
    }

    private static void validate(MoneyCostsEvent event) {
        if (event.schemaVersion() != MoneyCostsEvent.CURRENT_VERSION) {
            throw new UnsupportedEventException("Unsupported schemaVersion: " + event.schemaVersion());
        }
        if (event.eventId() == null || event.occurredAt() == null || event.userEmail() == null) {
            throw new UnsupportedEventException("Required fields are missing");
        }
    }
}
