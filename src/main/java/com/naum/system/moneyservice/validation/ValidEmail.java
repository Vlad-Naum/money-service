package com.naum.system.moneyservice.validation;

import com.naum.system.moneyservice.utils.EmailRules;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.RECORD_COMPONENT})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidEmailValidator.class)
public @interface ValidEmail {
    String message() default EmailRules.INVALID_EMAIL_MESSAGE;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
