package com.naum.system.moneyservice.service.kafka;

import com.naum.system.moneyservice.domain.money.MoneyCosts;
import com.naum.system.moneyservice.domain.money.MoneyCostsCategory;
import com.naum.system.moneyservice.domain.money.MoneyCostsKafka;
import com.naum.system.moneyservice.domain.user.User;
import com.naum.system.moneyservice.domain.user.UserCreateDto;
import com.naum.system.moneyservice.service.money.MoneyCostsService;
import com.naum.system.moneyservice.service.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaListenerService {

    @Autowired
    private MoneyCostsService moneyCostsService;

    @Autowired
    private UserService userService;

    @KafkaListener(topics = "money_service", groupId = "group1")
    void listener(MoneyCostsKafka moneyCostsKafka) {
        String email = moneyCostsKafka.getUserEmail();
        MoneyCostsCategory moneyCostsCategory = MoneyCostsCategory.getById(moneyCostsKafka.getMoneyCostsCategoryId());
        if (moneyCostsCategory == null) {
            moneyCostsCategory = MoneyCostsCategory.getDefault();
        }
        User user = userService.findUserByEmail(email);
        if (user == null) {
            user = userService.create(new UserCreateDto("", email));
            log.info("Create user by email: {}", email);
        }
        MoneyCosts moneyCosts = moneyCostsService.create(user, moneyCostsKafka.getLocalDateTime(), moneyCostsKafka.getExpenses(), moneyCostsCategory);
        log.info("Create money costs [{}] for user: {}", moneyCosts.toString(), email);
    }
}
