package com.naum.system.moneyservice.service.exception;

import com.naum.system.moneyservice.validation.EmailRules;

public class InvalidEmailException extends RuntimeException {

    public InvalidEmailException() {
        super(EmailRules.INVALID_EMAIL_MESSAGE);
    }
}
