package com.naum.system.moneyservice.validation;

import com.naum.system.moneyservice.utils.EmailRules;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidEmailValidator implements ConstraintValidator<ValidEmail, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value == null || EmailRules.isValid(value);
    }
}
