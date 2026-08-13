package com.devlab.pdf_wizard.application.in.command;

import java.util.Objects;

import com.devlab.pdf_wizard.domain.model.Email;

public record LoginCommand(Email email, String password) {

    public LoginCommand {
        Objects.requireNonNull(email, "Email cannot be null");
    }

    public static LoginCommand of(String email, String password) {
        return new LoginCommand(Email.of(email), password);
    }

    @Override
    public String toString() {
        return "LoginCommand[email=" + email + ", password=PROTECTED]";
    }
}
