package com.naum.system.moneyservice.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.naum.system.moneyservice.service.exception.InvalidEmailException;
import com.naum.system.moneyservice.service.exception.UnsupportedEventException;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@EnableKafka
@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic moneyCostsTopic() {
        return TopicBuilder.name("money_service").partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic moneyCostsDlt() {
        return TopicBuilder.name("money_service.DLT").partitions(3).replicas(1).build();
    }

    @Bean
    public DefaultErrorHandler kafkaErrorHandler(KafkaTemplate<String, String> template) {
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(template);
        DefaultErrorHandler handler = new DefaultErrorHandler(recoverer, new FixedBackOff(1_000L, 3));
        handler.addNotRetryableExceptions(InvalidEmailException.class, JsonProcessingException.class, UnsupportedEventException.class);
        return handler;
    }
}
