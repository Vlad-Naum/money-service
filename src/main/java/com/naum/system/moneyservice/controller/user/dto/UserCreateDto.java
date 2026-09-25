package com.naum.system.moneyservice.controller.user.dto;

import com.naum.system.moneyservice.validation.ValidEmail;
import jakarta.validation.constraints.NotBlank;
import org.springframework.lang.Nullable;

public record UserCreateDto(@Nullable
                            String name,
                            @ValidEmail
                            @NotBlank(message = "Email is required")
                            String email) {
}
