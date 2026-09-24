package com.naum.system.moneyservice.service.user;

import com.naum.system.moneyservice.domain.user.User;
import com.naum.system.moneyservice.domain.user.UserCreateDto;
import com.naum.system.moneyservice.repository.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
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

        User created = userService.create(new UserCreateDto("Ivan", "ivan@test.com"));

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertThat(captor.getValue().getName()).isEqualTo("Ivan");
        assertThat(captor.getValue().getEmail()).isEqualTo("ivan@test.com");
        assertThat(created).isSameAs(captor.getValue());
    }

    @Test
    void create_withInvalidEmail_throwsAndDoesNotSave() {
        assertThatThrownBy(() -> userService.create(new UserCreateDto("Ivan", "not-an-email")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User email is not valid");

        verify(userRepository, never()).save(any());
    }

    @Test
    void findUserById_withNullId_throws() {
        assertThatThrownBy(() -> userService.findUserById(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void findUserByEmail_withNullEmail_throws() {
        assertThatThrownBy(() -> userService.findUserByEmail(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void findAllUser_returnsAllUsersFromRepository() {
        User first = user(1L, "first@test.com");
        User second = user(2L, "second@test.com");
        when(userRepository.findAll()).thenReturn(List.of(first, second));

        assertThat(userService.findAllUser()).containsExactly(first, second);
    }

    @Test
    void deleteUserById_withNullId_returnsFalseAndDoesNotTouchRepository() {
        assertThat(userService.deleteUserById(null)).isFalse();

        verifyNoInteractions(userRepository);
    }

    @Test
    void deleteUserById_deletesAndReturnsTrue() {
        assertThat(userService.deleteUserById(5L)).isTrue();

        verify(userRepository).deleteById(5L);
    }

    private static User user(Long id, String email) {
        User user = new User();
        user.setId(id);
        user.setEmail(email);
        return user;
    }
}
