package com.naum.system.moneyservice.service.kafka.message;

import com.naum.system.contract.MoneyCostsEvent;
import com.naum.system.moneyservice.domain.money.MoneyCostsCategory;

import java.time.Instant;
import java.util.UUID;

public record RegisterExpenseCommand(
        UUID eventId,
        Instant occurredAt,
        String userEmail,
        long expenses,
        MoneyCostsCategory category) {

    public static RegisterExpenseCommand of(MoneyCostsEvent event) {
        return new RegisterExpenseCommand(
                event.eventId(),
                event.occurredAt(),
                event.userEmail(),
                event.expenses(),
                MoneyCostsCategory.getOrDefault(event.category()));
    }
}
