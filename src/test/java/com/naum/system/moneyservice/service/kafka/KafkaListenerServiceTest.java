package com.naum.system.moneyservice.service.kafka;

import com.naum.system.moneyservice.domain.money.MoneyCosts;
import com.naum.system.moneyservice.domain.user.User;
import com.naum.system.moneyservice.service.exception.InvalidEmailException;
import com.naum.system.moneyservice.service.kafka.message.MoneyCostsKafka;
import com.naum.system.moneyservice.service.money.MoneyCostsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KafkaListenerServiceTest {

    @Mock
    private MoneyCostsService moneyCostsService;

    @InjectMocks
    private KafkaListenerService listenerService;

    @Test
    void listener_delegatesMessageToService() {
        MoneyCostsKafka message = message("ivan@test.com");
        when(moneyCostsService.registerExpense(message)).thenReturn(savedCost());

        listenerService.listener(message);

        verify(moneyCostsService).registerExpense(message);
        verifyNoMoreInteractions(moneyCostsService);
    }

    @Test
    void listener_doesNotSwallowServiceExceptions() {
        // Исключение должно дойти до контейнера Kafka: только тогда сработают повторы, error handler и DLT.
        // Если листенер его поймает и залогирует, сообщение будет молча потеряно.
        MoneyCostsKafka message = message("not-an-email");
        when(moneyCostsService.registerExpense(message)).thenThrow(new InvalidEmailException());

        assertThatThrownBy(() -> listenerService.listener(message))
                .isInstanceOf(InvalidEmailException.class);
    }

    private static MoneyCostsKafka message(String email) {
        return MoneyCostsKafka.builder()
                .moneyCostsCategoryId(2)
                .expenses(1500L)
                .localDateTime(LocalDateTime.of(2024, 5, 1, 10, 15))
                .userEmail(email)
                .build();
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
