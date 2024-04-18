package com.naum.system.moneyservice.domain.user;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public record UserCreate(@Nullable String name, @NonNull String email) {
}
