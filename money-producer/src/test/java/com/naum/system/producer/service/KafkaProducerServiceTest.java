package com.naum.system.producer.service;

import com.naum.system.producer.domain.MoneyCostsKafka;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class KafkaProducerServiceTest {

    private static final String TOPIC = "money_service";

    @Mock
    private KafkaTemplate<String, MoneyCostsKafka> moneyCostsKafkaTemplate;

    @InjectMocks
    private KafkaProducerService producerService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(producerService, "topicName", TOPIC);
        // Незавершённый future: проверяем только то, что и куда отправлено
        lenient().when(moneyCostsKafkaTemplate.send(anyString(), any(MoneyCostsKafka.class)))
                .thenReturn(new CompletableFuture<>());
        lenient().when(moneyCostsKafkaTemplate.send(anyString(), anyString(), any(MoneyCostsKafka.class)))
                .thenReturn(new CompletableFuture<>());
    }

    @Test
    void sendMessage_sendsMessageToConfiguredTopic() {
        MoneyCostsKafka message = message();

        producerService.sendMessage(message);

        verify(moneyCostsKafkaTemplate).send(eq(TOPIC), same(message));
    }

    @Disabled("Задача 20: сообщения без ключа — порядок событий одного пользователя не гарантирован")
    @Test
    void sendMessage_usesUserEmailAsKey() {
        MoneyCostsKafka message = message();

        producerService.sendMessage(message);

        verify(moneyCostsKafkaTemplate).send(eq(TOPIC), eq("ivan@test.com"), same(message));
    }

    private static MoneyCostsKafka message() {
        return MoneyCostsKafka.builder()
                .moneyCostsCategoryId(2)
                .expenses(1500)
                .localDateTime(LocalDateTime.of(2024, 5, 1, 10, 15))
                .userEmail("ivan@test.com")
                .build();
    }
}
