package com.devlab.pdf_wizard.application.in.command;

import java.util.Objects;

import com.devlab.pdf_wizard.domain.model.Email;

public record RegisterUserCommand(Email email, String password) {

    public RegisterUserCommand {
        Objects.requireNonNull(email, "Email cannot be null");
    }

    public static RegisterUserCommand of(String email, String password) {
        return new RegisterUserCommand(
                Email.of(email),
                password);
    }

    @Override
    public String toString() {
        return "RegisterUserCommand[email=" + email + ", password=PROTECTED]";
    }
}
