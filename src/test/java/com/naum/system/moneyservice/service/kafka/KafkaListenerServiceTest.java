package com.naum.system.moneyservice.service.kafka;

import com.naum.system.moneyservice.domain.money.MoneyCosts;
import com.naum.system.moneyservice.domain.money.MoneyCostsCategory;
import com.naum.system.moneyservice.service.kafka.message.MoneyCostsKafka;
import com.naum.system.moneyservice.domain.user.User;
import com.naum.system.moneyservice.controller.user.dto.UserCreateDto;
import com.naum.system.moneyservice.service.money.MoneyCostsService;
import com.naum.system.moneyservice.service.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KafkaListenerServiceTest {

    private static final LocalDateTime DATE_TIME = LocalDateTime.of(2024, 5, 1, 10, 15);

    @Mock
    private MoneyCostsService moneyCostsService;

    @Mock
    private UserService userService;

    @InjectMocks
    private KafkaListenerService listenerService;

    @BeforeEach
    void stubMoneyCostsCreation() {
        // Листенер логирует результат, а MoneyCosts.toString() обращается к user.getId(),
        // поэтому мок должен вернуть сущность с заполненным пользователем.
        when(moneyCostsService.create(any(), any(), any(), any())).thenAnswer(invocation -> {
            MoneyCosts moneyCosts = new MoneyCosts();
            moneyCosts.setUser(invocation.getArgument(0));
            moneyCosts.setDateTime(invocation.getArgument(1));
            moneyCosts.setExpenses(invocation.getArgument(2));
            moneyCosts.setMoneyCostsCategory(invocation.getArgument(3));
            return moneyCosts;
        });
    }

    @Test
    void listener_forExistingUser_createsMoneyCostsWithoutCreatingUser() {
        User user = user(1L, "ivan@test.com");
        when(userService.findByEmail("ivan@test.com")).thenReturn(Optional.of(user));

        listenerService.listener(message(2, 1500L, "ivan@test.com"));

        verify(userService, never()).create(any(), any());
        verify(moneyCostsService).create(user, DATE_TIME, 1500L, MoneyCostsCategory.TAXI);
    }

    @Test
    void listener_forUnknownEmail_createsUserWithEmptyName() {
        User created = user(7L, "new@test.com");
        when(userService.create("", "new@test.com")).thenReturn(created);

        listenerService.listener(message(0, 300L, "new@test.com"));

        verify(userService).create("", "new@test.com");
        verify(moneyCostsService).create(created, DATE_TIME, 300L, MoneyCostsCategory.SUPERMARKETS);
    }

    @Test
    void listener_forUnknownCategoryId_usesDefaultCategory() {
        User user = user(1L, "ivan@test.com");
        when(userService.findByEmail("ivan@test.com")).thenReturn(Optional.of(user));

        listenerService.listener(message(100, 700L, "ivan@test.com"));

        verify(moneyCostsService).create(user, DATE_TIME, 700L, MoneyCostsCategory.OTHER);
    }

    private static MoneyCostsKafka message(int categoryId, long expenses, String email) {
        return MoneyCostsKafka.builder()
                .moneyCostsCategoryId(categoryId)
                .expenses(expenses)
                .localDateTime(DATE_TIME)
                .userEmail(email)
                .build();
    }

    private static User user(Long id, String email) {
        User user = new User();
        user.setId(id);
        user.setEmail(email);
        return user;
    }
}
