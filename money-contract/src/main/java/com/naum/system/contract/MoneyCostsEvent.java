package com.naum.system.contract;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.Instant;
import java.util.UUID;

public record MoneyCostsEvent(
        int schemaVersion,
        UUID eventId,
        @JsonFormat(shape = JsonFormat.Shape.STRING) Instant occurredAt,
        String userEmail,
        long expenses,
        String category) {

    public static final int CURRENT_VERSION = 2;

    @Override
    public String toString() {
        return "MoneyCostsEvent{" +
                "schemaVersion=" + schemaVersion +
                ", eventId=" + eventId +
                ", occurredAt=" + occurredAt +
                ", userEmail='" + userEmail + '\'' +
                ", expenses=" + expenses +
                ", category='" + category + '\'' +
                '}';
    }
}
