package com.naum.system.moneyservice.validation;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class EmailRulesTest {

    @ParameterizedTest(name = "[{index}] \"{0}\" — валидный")
    @ValueSource(strings = {
            "ivan@test.com",
            "ivan.petrov@test.com",
            "ivan+tag@mail.example.org",
            "IVAN@TEST.COM",
            "i@t.co"
    })
    void isValid_returnsTrue_forValidEmails(String email) {
        assertThat(EmailRules.isValid(email)).isTrue();
    }

    @ParameterizedTest(name = "[{index}] \"{0}\" — невалидный")
    @NullAndEmptySource
    @ValueSource(strings = {
            " ",
            "not-an-email",
            "ivan@",
            "@test.com",
            "ivan@test",
            "ivan@test.",
            "ivan@@test.com",
            "ivan@te@st.com",
            "ivan petrov@test.com",
            "ivan@test .com"
    })
    void isValid_returnsFalse_forInvalidEmails(String email) {
        assertThat(EmailRules.isValid(email)).isFalse();
    }
}
