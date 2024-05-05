package com.naum.system.producer.service;

import com.naum.system.producer.domain.MoneyCostsKafka;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class KafkaProducerService {

    Logger log = LoggerFactory.getLogger(KafkaProducerService.class);

    @Value(value = "${spring.kafka.topic.name}")
    private String topicName;

    @Autowired
    private KafkaTemplate<String, MoneyCostsKafka> moneyCostsKafkaTemplate;

    public void sendMessage(MoneyCostsKafka moneyCostsKafka) {
        CompletableFuture<SendResult<String, MoneyCostsKafka>> future = moneyCostsKafkaTemplate.send(topicName, moneyCostsKafka);
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Sent success with offset=[{}]", result.getRecordMetadata().offset());
            } else {
                log.error("Unable to send",  ex);
            }
        });
    }
}
