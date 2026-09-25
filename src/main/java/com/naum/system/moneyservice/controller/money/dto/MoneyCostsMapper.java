package com.naum.system.moneyservice.controller.money.dto;

import com.naum.system.moneyservice.domain.money.MoneyCosts;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MoneyCostsMapper {

    @Mapping(target = "localDateTime", source = "dateTime")
    MoneyCostsDto toDto(MoneyCosts moneyCosts);
}
