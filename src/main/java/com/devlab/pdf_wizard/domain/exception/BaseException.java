package com.devlab.pdf_wizard.domain.exception;

import org.springframework.http.HttpStatus;

import java.util.Map;

public abstract class BaseException extends RuntimeException {
    private final String code;
    private final HttpStatus status;
    private final Map<String, String> validationErrors;
    private final Map<String, Object> details;

    protected BaseException(String message) {
        super(message);
        this.code = CommonErrorType.INTERNAL_ERROR.getCode();
        this.status = CommonErrorType.INTERNAL_ERROR.getHttpStatus();
        this.validationErrors = null;
        this.details = null;
    }

    protected BaseException(String message, Throwable cause) {
        this(CommonErrorType.INTERNAL_ERROR.getCode(), CommonErrorType.INTERNAL_ERROR.getHttpStatus(), message, cause,
                null, null);
    }

    protected BaseException(ErrorType errorType) {
        this(errorType.getCode(), errorType.getHttpStatus(), errorType.getMessage(), null, null);
    }

    protected BaseException(ErrorType errorType, Map<String, String> validationErrors) {
        this(errorType.getCode(), errorType.getHttpStatus(), errorType.getMessage(), validationErrors, null);
    }

    protected BaseException(ErrorType errorType, Map<String, String> validationErrors, Map<String, Object> details) {
        this(errorType.getCode(), errorType.getHttpStatus(), errorType.getMessage(), validationErrors, details);
    }

    protected BaseException(String code, HttpStatus status, String message,
            Map<String, String> validationErrors, Map<String, Object> details) {
        super(message);
        this.code = code;
        this.status = status;
        this.validationErrors = validationErrors;
        this.details = details;
    }

    protected BaseException(String code, HttpStatus status, String message) {
        this(code, status, message, null, null);
    }

    protected BaseException(String code, HttpStatus status, String message,
            Map<String, String> validationErrors) {
        this(code, status, message, validationErrors, null);
    }

    protected BaseException(String code, HttpStatus status, String message,
            Throwable cause) {
        this(code, status, message, cause, null, null);
    }

    protected BaseException(String code, HttpStatus status, String message,
            Throwable cause, Map<String, String> validationErrors, Map<String, Object> details) {
        super(message, cause);
        this.code = code;
        this.status = status;
        this.validationErrors = validationErrors;
        this.details = details;
    }

    public String getCode() {
        return code;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public Map<String, String> getValidationErrors() {
        return validationErrors;
    }

    public Map<String, Object> getDetails() {
        return details;
    }
}
