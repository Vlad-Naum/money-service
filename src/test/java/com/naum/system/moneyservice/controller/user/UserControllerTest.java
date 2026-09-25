package com.naum.system.moneyservice.controller.user;

import com.naum.system.moneyservice.config.AppConfig;
import com.naum.system.moneyservice.domain.user.User;
import com.naum.system.moneyservice.service.exception.UserNotFoundException;
import com.naum.system.moneyservice.service.user.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Слайс-тест веб-слоя: поднимается только MVC и UserController, сервис замокан.
 * Тесты фиксируют текущий контракт API (включая пути со слэшем на конце).
 * Тесты с {@code @Disabled} описывают желаемое поведение — снимай аннотацию после соответствующей задачи.
 */
@WebMvcTest(UserController.class)
@Import(AppConfig.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    void getAll_returnsUsers() throws Exception {
        when(userService.findAllUser()).thenReturn(new ArrayList<>(List.of(
                user(1L, "Ivan", "ivan@test.com"),
                user(2L, null, "petr@test.com"))));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Ivan"))
                .andExpect(jsonPath("$[0].email").value("ivan@test.com"))
                .andExpect(jsonPath("$[1].email").value("petr@test.com"));
    }

    @Test
    void getById_whenUserExists_returnsUser() throws Exception {
        when(userService.getUserById(1L)).thenReturn(user(1L, "Ivan", "ivan@test.com"));

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("ivan@test.com"));
    }

    @Test
    void getById_whenUserNotFound_returns404Problem() throws Exception {
        when(userService.getUserById(42L)).thenThrow(UserNotFoundException.userNotFoundByIdException(42L));

        mockMvc.perform(get("/users/42"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value(containsString("42")));
    }


    @Test
    void getById_withNonNumericId_returns400() throws Exception {
        mockMvc.perform(get("/users/abc"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userService);
    }

    @Test
    void create_returns201WithId() throws Exception {
        when(userService.create(any(), any())).thenReturn(user(1L, null, "new@test.com"));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"new@test.com"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(content().string("1"));

        verify(userService).create(null, "new@test.com");
    }

    @Test
    void create_whenServiceRejectsEmail_returns400() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"not-an-email"}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAll_withoutTrailingSlash_returns200() throws Exception {
        when(userService.findAllUser()).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk());
    }

    @Test
    void create_withoutEmail_returns400WithoutCallingService() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Ivan"}
                                """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userService);
    }

    @Test
    void create_withInvalidEmail_problemContainsFieldError() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Ivan","email":"not-an-email"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.email").exists());
    }


    @Test
    void delete_byPathVariable_returns204() throws Exception {
        delete("/users/42");

        mockMvc.perform(delete("/users/42"))
                .andExpect(status().isNoContent());
    }

    private static User user(Long id, String name, String email) {
        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setEmail(email);
        return user;
    }
}
