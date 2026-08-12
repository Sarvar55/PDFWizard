package com.devlab.pdf_wizard.adapter.in.web.model;

import java.time.Instant;
import java.util.Map;

import com.devlab.pdf_wizard.domain.exception.BaseException;

public record ApiErrorResponse(
        String code,
        String message,
        Map<String, String> validationErrors,
        Map<String, Object> details,
        Instant timestamp) {

    public static ApiErrorResponse from(BaseException exception) {
        return new ApiErrorResponse(
                exception.getCode(),
                exception.getMessage(),
                Map.copyOf(exception.getValidationErrors()),
                Map.copyOf(exception.getDetails()),
                Instant.now());
    }

}
