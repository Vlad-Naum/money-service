package com.naum.system.producer.service;

import com.naum.system.producer.domain.MoneyCostsCategory;
import com.naum.system.producer.domain.MoneyCostsKafka;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ScheduledServiceTest {

    private static final int RUNS = 50;

    @Mock
    private KafkaProducerService producerService;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private ScheduledService scheduledService;

    @Test
    void scheduleKafkaProduced_sendsValidRandomMessages() {
        List<String> emails = List.of("test@test.com", "ivan@test.com");
        when(emailService.getEmails()).thenReturn(emails);

        for (int i = 0; i < RUNS; i++) {
            scheduledService.scheduleKafkaProduced();
        }

        ArgumentCaptor<MoneyCostsKafka> captor = ArgumentCaptor.forClass(MoneyCostsKafka.class);
        verify(producerService, times(RUNS)).sendMessage(captor.capture());
        assertThat(captor.getAllValues()).allSatisfy(message -> {
            assertThat(MoneyCostsCategory.getById(message.getMoneyCostsCategoryId())).isNotNull();
            assertThat(message.getExpenses()).isBetween(0L, 99_999L);
            assertThat(message.getUserEmail()).isIn(emails);
            assertThat(message.getLocalDateTime()).isNotNull();
        });
    }
}
