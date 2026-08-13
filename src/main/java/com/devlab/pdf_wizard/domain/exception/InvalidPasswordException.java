package com.devlab.pdf_wizard.domain.exception;

import java.util.Map;

public class InvalidPasswordException extends BaseException {

    public InvalidPasswordException(String message) {
        super(
                AuthErrorType.INVALID_PASSWORD,
                Map.of("password", message));
    }
}
