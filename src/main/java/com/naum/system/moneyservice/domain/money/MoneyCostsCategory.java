package com.naum.system.moneyservice.domain.money;

import org.springframework.lang.Nullable;

public enum MoneyCostsCategory {

    SUPERMARKETS(1),
    AUTO(2),
    TAXI(3),
    MARKETPLACE(4),
    CLOTHING(5),
    RESTAURANTS(6),
    BEAUTY(7),
    ENTERTAINMENT(8);

    private final int id;

    MoneyCostsCategory(int id) {
        this.id = id;
    }

    public @Nullable MoneyCostsCategory getById(int id) {
        for (MoneyCostsCategory value : MoneyCostsCategory.values()) {
            if (value.id == id) {
                return value;
            }
        }
        return null;
    }
}
