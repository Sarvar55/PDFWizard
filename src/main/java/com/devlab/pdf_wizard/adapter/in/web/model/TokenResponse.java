package com.devlab.pdf_wizard.adapter.in.web.model;

import java.time.Instant;
import java.util.Objects;

import com.devlab.pdf_wizard.application.model.AccessToken;

public record TokenResponse(
        String accessToken,
        String tokenType,
        Instant expiresAt) {

    private static final String BEARER = "Bearer";

    public static TokenResponse from(AccessToken token) {
        Objects.requireNonNull(token, "Access token cannot be null");
        return new TokenResponse(token.value(), BEARER, token.expiresAt());
    }
}
