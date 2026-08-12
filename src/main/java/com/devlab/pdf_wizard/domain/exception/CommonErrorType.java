package com.devlab.pdf_wizard.domain.exception;

import org.springframework.http.HttpStatus;

public enum CommonErrorType implements ErrorType {

    VALIDATION_FAILED("VALIDATION_FAILED", "Validation failed", HttpStatus.BAD_REQUEST),
    RESOURCE_NOT_FOUND("RESOURCE_NOT_FOUND", "Resource not found", HttpStatus.NOT_FOUND),
    UNAUTHORIZED("SECURITY_001", "Unauthorized", HttpStatus.UNAUTHORIZED),
    FORBIDDEN("SECURITY_002", "Access denied", HttpStatus.FORBIDDEN),
    INTERNAL_ERROR("COMMON_999", "Unexpected server error", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String code;
    private final String message;
    private final HttpStatus status;

    CommonErrorType(String code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return status;
    }
}