package com.medico.medico.util;

import java.util.regex.Pattern;

public final class PasswordComplexityValidator {

    private static final Pattern COMPLEXITY_PATTERN = Pattern.compile(
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()\\[\\]{}\\\\/\\\"'<>.,:;_+=|~-]).{8,}$");

    private PasswordComplexityValidator() {
    }

    public static void validate(String password) {
        if (password == null || !COMPLEXITY_PATTERN.matcher(password).matches()) {
            throw new IllegalArgumentException(
                    "Password must be at least 8 characters and include uppercase, lowercase, digit and special character.");
        }
    }
}
