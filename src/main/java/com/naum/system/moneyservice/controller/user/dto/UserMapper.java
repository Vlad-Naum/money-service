package com.naum.system.moneyservice.controller.user.dto;

import com.naum.system.moneyservice.domain.user.User;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User user);
    List<UserDto> toDto(List<User> users);
}
