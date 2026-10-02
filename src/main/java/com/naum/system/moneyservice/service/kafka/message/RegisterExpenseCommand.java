package com.naum.system.moneyservice.service.kafka.message;

import com.naum.system.moneyservice.domain.money.MoneyCostsCategory;

import java.time.Instant;
import java.util.UUID;

public record RegisterExpenseCommand(
        UUID eventId,
        Instant occurredAt,
        String userEmail,
        long expenses,
        MoneyCostsCategory category) {
}
