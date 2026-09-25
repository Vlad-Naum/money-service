package com.naum.system.moneyservice.controller.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.lang.Nullable;

public record UserCreateDto(@Nullable
                            String name,
                            @Email(message = "Email is invalid")
                            @NotBlank(message = "Email is required")
                            String email) {
}
