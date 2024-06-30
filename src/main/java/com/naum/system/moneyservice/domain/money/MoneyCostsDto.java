package com.naum.system.moneyservice.domain.money;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class MoneyCostsDto {

    private Long id;

    private MoneyCostsCategory moneyCostsCategory;

    private Long expenses;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime localDateTime;
}
