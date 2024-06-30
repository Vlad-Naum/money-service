package com.naum.system.moneyservice.domain.money;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public enum MoneyCostsCategory {

    SUPERMARKETS(0),
    AUTO(1),
    TAXI(2),
    MARKETPLACE(3),
    CLOTHING(4),
    RESTAURANTS(5),
    BEAUTY(6),
    ENTERTAINMENT(7),
    OTHER(8);

    private final int id;

    MoneyCostsCategory(int id) {
        this.id = id;
    }

    public static @Nullable MoneyCostsCategory getById(int id) {
        for (MoneyCostsCategory value : MoneyCostsCategory.values()) {
            if (value.id == id) {
                return value;
            }
        }
        return null;
    }

    public static @NonNull MoneyCostsCategory getDefault() {
        return OTHER;
    }
}
