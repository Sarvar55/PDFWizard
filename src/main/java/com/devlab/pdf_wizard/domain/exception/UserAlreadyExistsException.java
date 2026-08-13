package com.devlab.pdf_wizard.domain.exception;

import java.util.Map;

import com.devlab.pdf_wizard.domain.model.Email;

public class UserAlreadyExistsException extends BaseException {

    private UserAlreadyExistsException(Email email) {
        super(
                AuthErrorType.USER_ALREADY_EXISTS,
                Map.of("email", "Email is already registered"),
                Map.of("email", email.value()));
    }

    public static UserAlreadyExistsException forEmail(Email email) {
        return new UserAlreadyExistsException(email);
    }
}
