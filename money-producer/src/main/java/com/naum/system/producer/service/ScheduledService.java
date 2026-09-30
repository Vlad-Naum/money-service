package com.naum.system.producer.service;

import com.naum.system.producer.domain.MoneyCostsCategory;
import com.naum.system.producer.domain.MoneyCostsKafka;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
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
        MoneyCostsKafka moneyCostsKafka = new MoneyCostsKafka();
        moneyCostsKafka.setMoneyCostsCategory(MoneyCostsCategory.getRandomMoneyCostsCategory().name());
        moneyCostsKafka.setExpenses(ThreadLocalRandom.current().nextLong(100000L));
        moneyCostsKafka.setUserEmail(emails.get(ThreadLocalRandom.current().nextInt(0, emails.size())));
        moneyCostsKafka.setLocalDateTime(LocalDateTime.now());
        producerService.sendMessage(moneyCostsKafka);
    }
}
