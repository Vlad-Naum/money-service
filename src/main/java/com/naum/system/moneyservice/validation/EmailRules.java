package com.naum.system.moneyservice.validation;

import java.util.regex.Pattern;

public final class EmailRules {

    public static final String INVALID_EMAIL_MESSAGE = "Email is invalid";

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    private EmailRules() {
    }

    public static boolean isValid(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }
}