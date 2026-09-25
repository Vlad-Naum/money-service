package com.naum.system.moneyservice.service.exception;

import com.naum.system.moneyservice.utils.EmailRules;

public class InvalidEmailException extends RuntimeException {

    public InvalidEmailException() {
        super(EmailRules.INVALID_EMAIL_MESSAGE);
    }
}
