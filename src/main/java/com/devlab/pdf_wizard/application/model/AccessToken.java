package com.devlab.pdf_wizard.application.model;

import java.time.Instant;
import java.util.Objects;

public record AccessToken(String value, Instant expiresAt) {

    public AccessToken {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Access token cannot be null or empty");
        }
        Objects.requireNonNull(expiresAt, "Token expiration cannot be null");
    }
}
