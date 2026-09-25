package com.naum.system.moneyservice.service.user;

import com.naum.system.moneyservice.domain.user.User;
import com.naum.system.moneyservice.repository.user.UserRepository;
import com.naum.system.moneyservice.service.exception.InvalidEmailException;
import com.naum.system.moneyservice.service.exception.UserNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void create_savesUserWithNameAndEmail() {
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User created = userService.create("Ivan", "ivan@test.com");

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertThat(captor.getValue().getName()).isEqualTo("Ivan");
        assertThat(captor.getValue().getEmail()).isEqualTo("ivan@test.com");
        assertThat(created).isSameAs(captor.getValue());
    }

    @Test
    void create_withInvalidEmail_throwsAndDoesNotSave() {
        String invalidEmail = "not-an-email";
        assertThatThrownBy(() -> userService.create("Ivan", invalidEmail))
                .isInstanceOf(InvalidEmailException.class)
                .hasMessage("Email is invalid");

        verify(userRepository, never()).save(any());
    }

    @Test
    void getUserById_withNotFoundId_throws() {
        assertThatThrownBy(() -> userService.getUserById(1L))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void findAllUser_returnsAllUsersFromRepository() {
        User first = user(1L, "first@test.com");
        User second = user(2L, "second@test.com");
        when(userRepository.findAll()).thenReturn(List.of(first, second));

        assertThat(userService.findAllUser()).containsExactly(first, second);
    }

    @Test
    void deleteUserById() {
        userService.deleteUserById(5L);
        verify(userRepository).deleteById(5L);
    }

    private static User user(Long id, String email) {
        User user = new User();
        user.setId(id);
        user.setEmail(email);
        return user;
    }
}
