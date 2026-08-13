package com.devlab.pdf_wizard.adapter.in.web.model;

import com.devlab.pdf_wizard.application.in.command.RegisterUserCommand;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterUserRequest(
        @NotBlank(message = "Email cannot be blank")
        @Email(message = "Email format is invalid")
        String email,

        @NotBlank(message = "Password cannot be blank")
        @Size(min = 8, max = 72, message = "Password must contain between 8 and 72 characters")
        String password) {

    public RegisterUserCommand toCommand() {
        return RegisterUserCommand.of(email, password);
    }
}
