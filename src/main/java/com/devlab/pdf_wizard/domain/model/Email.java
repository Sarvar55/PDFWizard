package com.devlab.pdf_wizard.domain.model;

import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import com.devlab.pdf_wizard.domain.exception.CommonErrorType;
import com.devlab.pdf_wizard.domain.exception.ValidationException;

public final class Email {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$",
            Pattern.CASE_INSENSITIVE);

    private final String value;

    private Email(String value) {
        this.value = value;
    }

    public static Email of(String value) {
        if (value == null || value.isBlank()) {
            throw validationError("Email cannot be null or empty");
        }

        String normalizedValue = value.trim().toLowerCase(Locale.ROOT);
        if (!EMAIL_PATTERN.matcher(normalizedValue).matches()) {
            throw validationError("Email format is invalid");
        }

        return new Email(normalizedValue);
    }

    private static ValidationException validationError(String message) {
        return ValidationException.of(
                CommonErrorType.VALIDATION_FAILED,
                Map.of("email", message));
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof Email email)) {
            return false;
        }
        return value.equals(email.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value;
    }
}
