package com.naum.system.moneyservice.service.kafka;

import com.fasterxml.jackson.core.JsonParseException;
import com.naum.system.moneyservice.domain.money.MoneyCosts;
import com.naum.system.moneyservice.domain.money.MoneyCostsCategory;
import com.naum.system.moneyservice.domain.user.User;
import com.naum.system.moneyservice.service.exception.InvalidEmailException;
import com.naum.system.moneyservice.service.exception.UnsupportedEventException;
import com.naum.system.moneyservice.service.kafka.message.RegisterExpenseCommand;
import com.naum.system.moneyservice.service.money.MoneyCostsService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Листенер только связывает читателя сообщений и сервис. Формат сообщения проверяет
 * MoneyCostsEventReaderContractTest, бизнес-логику — MoneyCostsServiceTest,
 * всю цепочку — KafkaListenerIntegrationTest.
 */
@ExtendWith(MockitoExtension.class)
class KafkaListenerServiceTest {

    private static final String JSON = "{\"any\":\"payload\"}";

    @Mock
    private MoneyCostsEventReader eventReader;

    @Mock
    private MoneyCostsService moneyCostsService;

    private KafkaListenerService listenerService;

    @BeforeEach
    void setUp() {
        listenerService = new KafkaListenerService(moneyCostsService, eventReader);
    }

    @Test
    void listener_passesCommandFromReaderToService() throws Exception {
        RegisterExpenseCommand command = command();
        when(eventReader.read(JSON)).thenReturn(command);
        when(moneyCostsService.registerExpense(command)).thenReturn(savedCost());

        listenerService.listener(record(JSON));

        verify(moneyCostsService).registerExpense(command);
    }

    @Test
    void listener_whenMessageIsMalformed_propagatesExceptionAndDoesNotCallService() throws Exception {
        when(eventReader.read(JSON)).thenThrow(new JsonParseException(null, "Unexpected character"));

        assertThatThrownBy(() -> listenerService.listener(record(JSON)))
                .isInstanceOf(JsonParseException.class);

        verify(moneyCostsService, never()).registerExpense(any());
    }

    @Test
    void listener_whenEventIsUnsupported_propagatesExceptionAndDoesNotCallService() throws Exception {
        when(eventReader.read(JSON)).thenThrow(new UnsupportedEventException("Unsupported schemaVersion: 3"));

        assertThatThrownBy(() -> listenerService.listener(record(JSON)))
                .isInstanceOf(UnsupportedEventException.class);

        verify(moneyCostsService, never()).registerExpense(any());
    }

    @Test
    void listener_doesNotSwallowServiceExceptions() throws Exception {
        // Исключение должно дойти до контейнера Kafka: только тогда сработают повторы, error handler и DLT.
        // Если листенер его поймает и залогирует, сообщение будет молча потеряно.
        RegisterExpenseCommand command = command();
        when(eventReader.read(JSON)).thenReturn(command);
        when(moneyCostsService.registerExpense(command)).thenThrow(new InvalidEmailException());

        assertThatThrownBy(() -> listenerService.listener(record(JSON)))
                .isInstanceOf(InvalidEmailException.class);
    }

    private static ConsumerRecord<String, String> record(String value) {
        return new ConsumerRecord<>("money_service", 0, 0L, null, value);
    }

    private static RegisterExpenseCommand command() {
        return new RegisterExpenseCommand(
                UUID.randomUUID(),
                Instant.parse("2024-05-01T10:15:42Z"),
                "ivan@test.com",
                1500L,
                MoneyCostsCategory.TAXI);
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