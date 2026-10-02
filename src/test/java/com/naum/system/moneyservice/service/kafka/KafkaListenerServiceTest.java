package com.naum.system.moneyservice.service.kafka;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.naum.system.contract.MoneyCostsEvent;
import com.naum.system.moneyservice.domain.money.MoneyCosts;
import com.naum.system.moneyservice.domain.money.MoneyCostsCategory;
import com.naum.system.moneyservice.domain.user.User;
import com.naum.system.moneyservice.service.exception.InvalidEmailException;
import com.naum.system.moneyservice.service.kafka.message.RegisterExpenseCommand;
import com.naum.system.moneyservice.service.money.MoneyCostsService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KafkaListenerServiceTest {

    public static final String TOPIC = "money_service";

    private final ObjectMapper objectMapper = JsonMapper.builder()
            .findAndAddModules()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .build();

    @Mock
    private MoneyCostsService moneyCostsService;

    private KafkaListenerService listenerService;

    @BeforeEach
    void setUp() {
        listenerService = new KafkaListenerService(moneyCostsService, objectMapper);
    }

    @Test
    void listener_delegatesMessageToService() {
        MoneyCostsEvent message = message("ivan@test.com");
        RegisterExpenseCommand expenseCommand = RegisterExpenseCommand.of(message);
        when(moneyCostsService.registerExpense(expenseCommand)).thenReturn(savedCost());

        String messageJson = Assertions.assertDoesNotThrow(() -> objectMapper.writeValueAsString(message));
        Assertions.assertDoesNotThrow(() -> listenerService.listener(
                new ConsumerRecord<>(
                        TOPIC,
                        1,
                        1,
                        message.eventId().toString(),
                        messageJson)));

        verify(moneyCostsService).registerExpense(expenseCommand);
        verifyNoMoreInteractions(moneyCostsService);
    }

    @Test
    void listener_doesNotSwallowServiceExceptions() {
        // Исключение должно дойти до контейнера Kafka: только тогда сработают повторы, error handler и DLT.
        // Если листенер его поймает и залогирует, сообщение будет молча потеряно.
        MoneyCostsEvent message = message("not-an-email");
        RegisterExpenseCommand expenseCommand = RegisterExpenseCommand.of(message);
        String messageJson = Assertions.assertDoesNotThrow(() -> objectMapper.writeValueAsString(message));
        when(moneyCostsService.registerExpense(expenseCommand)).thenThrow(new InvalidEmailException());

        assertThatThrownBy(() -> listenerService.listener(
                new ConsumerRecord<>(
                        TOPIC,
                        1,
                        1,
                        message.eventId().toString(),
                        messageJson)))
                .isInstanceOf(InvalidEmailException.class);
    }

    private static MoneyCostsEvent message(String email) {
        return new MoneyCostsEvent(
                MoneyCostsEvent.CURRENT_VERSION,
                UUID.randomUUID(),
                Instant.parse("2024-05-01T10:15:00Z"),
                email,
                1500L,
                MoneyCostsCategory.TAXI.name()
        );
    }

    /**
     * Листенер логирует результат, а MoneyCosts.toString() обращается к пользователю,
     * поэтому возвращаем сущность с заполненным пользователем.
     */
    private static MoneyCosts savedCost() {
        User user = new User();
        user.setId(1L);
        MoneyCosts moneyCosts = new MoneyCosts();
        moneyCosts.setUser(user);
        return moneyCosts;
    }
}
