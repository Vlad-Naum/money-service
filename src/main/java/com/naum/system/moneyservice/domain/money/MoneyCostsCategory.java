package com.naum.system.moneyservice.domain.money;

import java.util.Arrays;
import java.util.Locale;

public enum MoneyCostsCategory {

    SUPERMARKETS,
    AUTO,
    TAXI,
    MARKETPLACE,
    CLOTHING,
    RESTAURANTS,
    BEAUTY,
    ENTERTAINMENT,
    OTHER;

    public static MoneyCostsCategory getOrDefault(String category) {
        if (category == null) {
            return OTHER;
        }
        return Arrays.stream(MoneyCostsCategory.values())
                .filter(category1 -> category1.name().equals(category.toUpperCase(Locale.ROOT)))
                .findFirst()
                .orElse(OTHER);
    }
}
