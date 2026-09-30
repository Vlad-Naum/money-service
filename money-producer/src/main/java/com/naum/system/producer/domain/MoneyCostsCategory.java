package com.naum.system.producer.domain;

import java.util.concurrent.ThreadLocalRandom;

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

    public static MoneyCostsCategory getRandomMoneyCostsCategory() {
        MoneyCostsCategory[] values = MoneyCostsCategory.values();
        return values[ThreadLocalRandom.current().nextInt(values.length)];
    }
}
