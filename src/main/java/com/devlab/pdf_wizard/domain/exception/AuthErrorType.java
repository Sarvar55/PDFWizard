package com.devlab.pdf_wizard.domain.exception;

import org.springframework.http.HttpStatus;

public enum AuthErrorType implements ErrorType {

    USER_ALREADY_EXISTS(
            "AUTH_001",
            "A user with this email already exists",
            HttpStatus.CONFLICT),
    INVALID_CREDENTIALS(
            "AUTH_002",
            "Email or password is incorrect",
            HttpStatus.UNAUTHORIZED),
    USER_DISABLED(
            "AUTH_003",
            "User account is disabled",
            HttpStatus.FORBIDDEN),
    INVALID_PASSWORD(
            "AUTH_004",
            "Password is invalid",
            HttpStatus.BAD_REQUEST);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;

    AuthErrorType(String code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
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
        return httpStatus;
    }
}
