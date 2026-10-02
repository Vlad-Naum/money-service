package com.naum.system.moneyservice.service.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.naum.system.contract.MoneyCostsEvent;
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

    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "money_service")
    void listener(ConsumerRecord<String, String> record) throws JsonProcessingException {
        JsonNode json = objectMapper.readTree(record.value());
        RegisterExpenseCommand command = RegisterExpenseCommand.of(
                objectMapper.treeToValue(json, MoneyCostsEvent.class));
        MoneyCosts moneyCosts = moneyCostsService.registerExpense(command);
        log.info("Create money costs [{}] for user: {}", moneyCosts.toString(), command.userEmail());
    }
}
