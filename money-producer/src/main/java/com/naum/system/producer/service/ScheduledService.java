package com.naum.system.producer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class ScheduledService {

    @Autowired
    private KafkaProducerService producerService;

    @Scheduled(fixedDelay = 60, timeUnit = TimeUnit.SECONDS, initialDelay = 0)
    public void scheduleKafkaProduced() {
        producerService.sendMessage("message");
    }
}
