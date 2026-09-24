package com.naum.system.moneyservice.controller.money;

import com.naum.system.moneyservice.config.AppConfig;
import com.naum.system.moneyservice.domain.money.MoneyCosts;
import com.naum.system.moneyservice.domain.money.MoneyCostsCategory;
import com.naum.system.moneyservice.service.money.MoneyCostsService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MoneyCostsController.class)
@Import(AppConfig.class)
class MoneyCostsControllerTest {

    private static final LocalDate DATE = LocalDate.of(2024, 5, 1);
    private static final String URL = "/users/1/money_costs/";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MoneyCostsService moneyCostsService;

    @Test
    void getWithoutCategory_returnsPageOfMoneyCosts() throws Exception {
        when(moneyCostsService.findAllByDateAndUserId(eq(DATE), eq(1L), any(Pageable.class)))
                .thenReturn(page(moneyCosts(10L, MoneyCostsCategory.TAXI, 1500L)));

        mockMvc.perform(get(URL).param("localDate", "2024-05-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].id").value(10))
                .andExpect(jsonPath("$.content[0].moneyCostsCategory").value("TAXI"))
                .andExpect(jsonPath("$.content[0].expenses").value(1500));

        verify(moneyCostsService, never()).findAllByDateAndUserIdAndCategory(any(), any(), any(), any());
    }

    @Test
    void getWithoutPagingParams_usesDefaultPageAndSortById() throws Exception {
        when(moneyCostsService.findAllByDateAndUserId(eq(DATE), eq(1L), any(Pageable.class)))
                .thenReturn(page());

        mockMvc.perform(get(URL).param("localDate", "2024-05-01"))
                .andExpect(status().isOk());

        Pageable pageable = capturePageable();
        assertThat(pageable.getPageNumber()).isZero();
        assertThat(pageable.getPageSize()).isEqualTo(20);
        assertThat(pageable.getSort()).isEqualTo(Sort.by(Sort.Direction.ASC, "id"));
    }

    @Test
    void getWithPagingParams_passesThemToService() throws Exception {
        when(moneyCostsService.findAllByDateAndUserId(eq(DATE), eq(1L), any(Pageable.class)))
                .thenReturn(page());

        mockMvc.perform(get(URL)
                        .param("localDate", "2024-05-01")
                        .param("page", "1")
                        .param("size", "5")
                        .param("sort", "expenses,desc"))
                .andExpect(status().isOk());

        Pageable pageable = capturePageable();
        assertThat(pageable.getPageNumber()).isEqualTo(1);
        assertThat(pageable.getPageSize()).isEqualTo(5);
        assertThat(pageable.getSort()).isEqualTo(Sort.by(Sort.Direction.DESC, "expenses"));
    }

    @Test
    void getWithCategory_filtersByCategory() throws Exception {
        when(moneyCostsService.findAllByDateAndUserIdAndCategory(eq(DATE), eq(1L), any(Pageable.class),
                eq(MoneyCostsCategory.TAXI)))
                .thenReturn(page(moneyCosts(10L, MoneyCostsCategory.TAXI, 1500L)));

        mockMvc.perform(get(URL)
                        .param("localDate", "2024-05-01")
                        .param("moneyCostsCategory", "TAXI"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].moneyCostsCategory").value("TAXI"));

        verify(moneyCostsService, never()).findAllByDateAndUserId(any(), any(), any());
    }

    @Test
    void getWithoutDate_returns400() throws Exception {
        mockMvc.perform(get(URL))
                .andExpect(status().isBadRequest());
    }

    @Disabled("Задача 7: в сущности поле dateTime, в DTO localDateTime — ModelMapper их не сопоставляет, в ответе null")
    @Test
    void response_containsDateTime() throws Exception {
        when(moneyCostsService.findAllByDateAndUserId(eq(DATE), eq(1L), any(Pageable.class)))
                .thenReturn(page(moneyCosts(10L, MoneyCostsCategory.TAXI, 1500L)));

        mockMvc.perform(get(URL).param("localDate", "2024-05-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].localDateTime").value("2024-05-01 10:15:00"));
    }

    @Disabled("Задача 3: путь без слэша на конце не матчится")
    @Test
    void getWithoutTrailingSlash_returns200() throws Exception {
        when(moneyCostsService.findAllByDateAndUserId(eq(DATE), eq(1L), any(Pageable.class)))
                .thenReturn(page());

        mockMvc.perform(get("/users/1/money_costs").param("localDate", "2024-05-01"))
                .andExpect(status().isOk());
    }

    private Pageable capturePageable() {
        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(moneyCostsService).findAllByDateAndUserId(eq(DATE), eq(1L), captor.capture());
        return captor.getValue();
    }

    private static Page<MoneyCosts> page(MoneyCosts... costs) {
        return new PageImpl<>(List.of(costs), PageRequest.of(0, 20), costs.length);
    }

    private static MoneyCosts moneyCosts(Long id, MoneyCostsCategory category, Long expenses) {
        MoneyCosts moneyCosts = new MoneyCosts();
        moneyCosts.setId(id);
        moneyCosts.setMoneyCostsCategory(category);
        moneyCosts.setExpenses(expenses);
        moneyCosts.setDateTime(LocalDateTime.of(2024, 5, 1, 10, 15));
        return moneyCosts;
    }
}
