package com.naum.system.moneyservice.domain.money;

import com.naum.system.moneyservice.domain.user.UserDto;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class MoneyCostsDto {

    private Long id;

    private MoneyCostsCategory moneyCostsCategory;

    private Long expenses;

    private LocalDateTime localDateTime;

    private UserDto user;
}
