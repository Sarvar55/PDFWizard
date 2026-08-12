package com.devlab.pdf_wizard.domain.exception;

import java.util.Map;

public class ValidationException extends BaseException {

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException() {
        this(CommonErrorType.VALIDATION_FAILED, null, null);
    }

    public ValidationException(ErrorType errorType) {
        this(errorType, null, null);
    }

    public ValidationException(ErrorType errorType, Map<String, String> validationErrors) {
        this(errorType, validationErrors, null);
    }

    public ValidationException(ErrorType errorType, Map<String, String> validationErrors,
            Map<String, Object> details) {
        super(errorType, validationErrors, details);
    }

    public static ValidationException of(Map<String, String> validationErrors) {
        return new ValidationException(CommonErrorType.VALIDATION_FAILED, validationErrors, null);
    }

    public static ValidationException of(ErrorType errorType) {
        return new ValidationException(errorType, null, null);
    }

    public static ValidationException of(ErrorType errorType, Map<String, String> validationErrors) {
        return new ValidationException(errorType, validationErrors, null);
    }

    public static ValidationException of(ErrorType errorType, Map<String, String> validationErrors,
            Map<String, Object> details) {
        return new ValidationException(errorType, validationErrors, details);
    }
}
