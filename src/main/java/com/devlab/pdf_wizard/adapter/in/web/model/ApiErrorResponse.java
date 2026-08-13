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
                copyOrNull(exception.getValidationErrors()),
                copyOrNull(exception.getDetails()),
                Instant.now());
    }

    private static <K, V> Map<K, V> copyOrNull(Map<K, V> values) {
        return values == null ? null : Map.copyOf(values);
    }

}
