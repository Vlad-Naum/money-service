package com.naum.system.producer.service;

import com.naum.system.contract.MoneyCostsEvent;
import com.naum.system.producer.config.MoneyProducerProperties;
import com.naum.system.producer.domain.MoneyCostsCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.Instant;
import java.util.UUID;
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
    private KafkaTemplate<String, MoneyCostsEvent> moneyCostsEventTemplate;

    private KafkaProducerService producerService;

    @BeforeEach
    void setUp() {
        producerService = new KafkaProducerService(
                new MoneyProducerProperties(null, new MoneyProducerProperties.Kafka(TOPIC)),
                moneyCostsEventTemplate);
        // Незавершённый future: проверяем только то, что и куда отправлено
        lenient().when(moneyCostsEventTemplate.send(anyString(), any(MoneyCostsEvent.class)))
                .thenReturn(new CompletableFuture<>());
        lenient().when(moneyCostsEventTemplate.send(anyString(), anyString(), any(MoneyCostsEvent.class)))
                .thenReturn(new CompletableFuture<>());
    }

    @Test
    void sendMessage_sendsMessageToConfiguredTopic() {
        MoneyCostsEvent event = event();

        producerService.sendMessage(event);

        verify(moneyCostsEventTemplate).send(eq(TOPIC), same(event));
    }

    @Disabled("Задача 20: сообщения без ключа — порядок событий одного пользователя не гарантирован")
    @Test
    void sendMessage_usesUserEmailAsKey() {
        MoneyCostsEvent event = event();

        producerService.sendMessage(event);

        verify(moneyCostsEventTemplate).send(eq(TOPIC), eq("ivan@test.com"), same(event));
    }

    private static MoneyCostsEvent event() {
        return new MoneyCostsEvent(
                MoneyCostsEvent.CURRENT_VERSION,
                UUID.randomUUID(),
                Instant.parse("2024-05-01T10:15:00Z"),
                "ivan@test.com",
                1500,
                MoneyCostsCategory.TAXI.name()
        );
    }
}
