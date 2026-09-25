package com.naum.system.moneyservice.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class ValidEmailValidatorTest {

    private final ValidEmailValidator validator = new ValidEmailValidator();

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   "})
    void isValid_skipsBlankValues(String value) {
        assertThat(validator.isValid(value, null)).isTrue();
    }

    @Test
    void isValid_delegatesToEmailRules() {
        assertThat(validator.isValid("ivan@test.com", null)).isTrue();
        assertThat(validator.isValid("not-an-email", null)).isFalse();
    }
}
