package com.naum.system.producer.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "money-producer")
public record MoneyProducerProperties(Email email, Kafka kafka) {

    public record Email(String url) {

    }

    public record Kafka(String topic) {

    }
}
