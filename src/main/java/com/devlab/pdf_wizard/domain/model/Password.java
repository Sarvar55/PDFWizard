package com.devlab.pdf_wizard.domain.model;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import com.devlab.pdf_wizard.domain.exception.CommonErrorType;
import com.devlab.pdf_wizard.domain.exception.ValidationException;

public final class Password {

    private static final int MIN_LENGTH = 8;
    private static final int MAX_UTF8_BYTES = 72;

    private final String value;

    private Password(String value) {
        this.value = value;
    }

    public static Password of(String value) {
        if (value == null || value.isBlank()) {
            throw validationError("Password cannot be null or empty");
        }
        if (value.length() < MIN_LENGTH) {
            throw validationError("Password must contain at least 8 characters");
        }
        if (value.getBytes(StandardCharsets.UTF_8).length > MAX_UTF8_BYTES) {
            throw validationError("Password cannot exceed 72 UTF-8 bytes");
        }

        return new Password(value);
    }

    private static ValidationException validationError(String message) {
        return ValidationException.of(
                CommonErrorType.VALIDATION_FAILED,
                Map.of("password", message));
    }

    public String value() {
        return value;
    }

    @Override
    public String toString() {
        return "[PROTECTED]";
    }
}
