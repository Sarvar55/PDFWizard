package com.devlab.pdf_wizard.domain.model;

import java.util.Map;

import com.devlab.pdf_wizard.domain.exception.CommonErrorType;
import com.devlab.pdf_wizard.domain.exception.ValidationException;

public final class PasswordHash {

    private final String value;

    private PasswordHash(String value) {
        this.value = value;
    }

    public static PasswordHash of(String value) {
        if (value == null || value.isBlank()) {
            throw ValidationException.of(
                    CommonErrorType.VALIDATION_FAILED,
                    Map.of("passwordHash", "Password hash cannot be null or empty"));
        }
        return new PasswordHash(value);
    }

    public String value() {
        return value;
    }

    @Override
    public String toString() {
        return "[PROTECTED]";
    }
}
