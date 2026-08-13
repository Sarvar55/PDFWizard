package com.devlab.pdf_wizard.domain.exception;

public class InvalidCredentialsException extends BaseException {

    public InvalidCredentialsException() {
        super(AuthErrorType.INVALID_CREDENTIALS);
    }
}
