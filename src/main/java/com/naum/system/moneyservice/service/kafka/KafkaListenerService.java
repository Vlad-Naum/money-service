package com.naum.system.moneyservice.service.kafka;

import com.naum.system.moneyservice.domain.money.MoneyCosts;
import com.naum.system.moneyservice.service.kafka.message.MoneyCostsKafka;
import com.naum.system.moneyservice.service.money.MoneyCostsService;
import com.naum.system.moneyservice.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaListenerService {

    private final MoneyCostsService moneyCostsService;

    private final UserService userService;

    @KafkaListener(topics = "money_service", groupId = "group1")
    void listener(MoneyCostsKafka moneyCostsKafka) {
        MoneyCosts moneyCosts = moneyCostsService.registerExpense(moneyCostsKafka);
        log.info("Create money costs [{}] for user: {}", moneyCosts.toString(), moneyCostsKafka.getUserEmail());
    }
}
