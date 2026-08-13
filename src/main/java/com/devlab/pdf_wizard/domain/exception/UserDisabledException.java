package com.devlab.pdf_wizard.domain.exception;

public class UserDisabledException extends BaseException {

    public UserDisabledException() {
        super(AuthErrorType.USER_DISABLED);
    }
}
