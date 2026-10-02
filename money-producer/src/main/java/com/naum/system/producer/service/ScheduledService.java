package com.naum.system.producer.service;

import com.naum.system.producer.domain.MoneyCostsCategory;
import com.naum.system.contract.MoneyCostsEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class ScheduledService {

    private final KafkaProducerService producerService;

    private final EmailService emailService;

    @Scheduled(fixedDelay = 60, timeUnit = TimeUnit.SECONDS, initialDelay = 0)
    public void scheduleKafkaProduced() {
        List<String> emails = emailService.getEmails();
        var event = new MoneyCostsEvent(
                MoneyCostsEvent.CURRENT_VERSION,
                UUID.randomUUID(),
                Instant.now(),
                emails.get(ThreadLocalRandom.current().nextInt(0, emails.size())),
                ThreadLocalRandom.current().nextLong(100000L),
                MoneyCostsCategory.getRandomMoneyCostsCategory().name()
        );
        producerService.sendMessage(event);
    }
}
