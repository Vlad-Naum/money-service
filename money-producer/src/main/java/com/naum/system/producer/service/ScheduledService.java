package com.naum.system.producer.service;

import com.naum.system.producer.domain.MoneyCostsCategory;
import com.naum.system.producer.domain.MoneyCostsKafka;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class ScheduledService {

    @Autowired
    private KafkaProducerService producerService;

    @Autowired
    private EmailService emailService;

    @Scheduled(fixedDelay = 60, timeUnit = TimeUnit.SECONDS, initialDelay = 0)
    public void scheduleKafkaProduced() {
        List<String> emails = emailService.getEmails();
        Random random = new Random();
        MoneyCostsKafka moneyCostsKafka = new MoneyCostsKafka();
        moneyCostsKafka.setMoneyCostsCategoryId(random.nextInt(0, MoneyCostsCategory.values().length));
        moneyCostsKafka.setExpenses(random.nextLong(100000));
        moneyCostsKafka.setUserEmail(emails.get(random.nextInt(0, emails.size())));
        moneyCostsKafka.setLocalDateTime(LocalDateTime.now());
        producerService.sendMessage(moneyCostsKafka);
    }
}
