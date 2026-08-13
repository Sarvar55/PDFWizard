package com.devlab.pdf_wizard.adapter.in.web.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import com.devlab.pdf_wizard.domain.model.Role;
import com.devlab.pdf_wizard.domain.model.User;

public record UserResponse(
        UUID id,
        String email,
        Role role,
        boolean enabled,
        Instant createdAt,
        Instant updatedAt) {

    public static UserResponse from(User user) {
        Objects.requireNonNull(user, "User cannot be null");

        return new UserResponse(
                user.getId(),
                user.getEmail().value(),
                user.getRole(),
                user.isEnabled(),
                user.getCreatedAt(),
                user.getUpdatedAt());
    }
}
