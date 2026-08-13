package com.devlab.pdf_wizard.application.model;

import java.security.Principal;
import java.util.Objects;
import java.util.UUID;

import com.devlab.pdf_wizard.domain.model.Email;
import com.devlab.pdf_wizard.domain.model.Role;

public record AuthenticatedUser(UUID id, Email email, Role role) implements Principal {

    public AuthenticatedUser {
        Objects.requireNonNull(id, "User id cannot be null");
        Objects.requireNonNull(email, "Email cannot be null");
        Objects.requireNonNull(role, "Role cannot be null");
    }

    @Override
    public String getName() {
        return email.value();
    }
}
