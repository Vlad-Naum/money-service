package com.naum.system.producer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MoneyProducerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MoneyProducerApplication.class, args);
    }
}