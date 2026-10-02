package com.naum.system.moneyservice.service.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.naum.system.moneyservice.domain.money.MoneyCosts;
import com.naum.system.moneyservice.service.kafka.message.RegisterExpenseCommand;
import com.naum.system.moneyservice.service.money.MoneyCostsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaListenerService {

    private final MoneyCostsService moneyCostsService;

    private final MoneyCostsEventReader eventReader;

    @KafkaListener(topics = "money_service")
    void listener(ConsumerRecord<String, String> record) throws JsonProcessingException {
        RegisterExpenseCommand command = eventReader.read(record.value());
        MoneyCosts moneyCosts = moneyCostsService.registerExpense(command);
        log.info("Create money costs [{}] for user: {}", moneyCosts.toString(), command.userEmail());
    }
}
