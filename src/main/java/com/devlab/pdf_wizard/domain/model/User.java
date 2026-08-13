package com.devlab.pdf_wizard.domain.model;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import com.devlab.pdf_wizard.domain.exception.CommonErrorType;
import com.devlab.pdf_wizard.domain.exception.ValidationException;

public class User {

    private final UUID id;
    private final Email email;
    private Password password;
    private final Role role;
    private boolean enabled;
    private final Instant createdAt;
    private Instant updatedAt;

    private User(UUID id, Email email, Password password, Role role,
            boolean enabled, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.role = role;
        this.enabled = enabled;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static User register(Email email, Password password) {
        Instant now = Instant.now();

        return new User(
                UUID.randomUUID(),
                validateEmail(email),
                validatePassword(password),
                Role.USER,
                true,
                now,
                now);
    }

    public static User restore(UUID id, Email email, Password password,
            Role role, boolean enabled, Instant createdAt, Instant updatedAt) {
        return new User(
                validateId(id),
                validateEmail(email),
                validatePassword(password),
                validateRole(role),
                enabled,
                validateTimestamp("createdAt", createdAt),
                validateTimestamp("updatedAt", updatedAt));
    }

    public void changePassword(Password newPassword) {
        this.password = validatePassword(newPassword);
        touch();
    }

    public void disable() {
        this.enabled = false;
        touch();
    }

    public void enable() {
        this.enabled = true;
        touch();
    }

    private void touch() {
        this.updatedAt = Instant.now();
    }

    private static UUID validateId(UUID id) {
        if (id == null) {
            throw validationError("id", "User id cannot be null");
        }
        return id;
    }

    private static Email validateEmail(Email email) {
        if (email == null) {
            throw validationError("email", "Email cannot be null");
        }
        return email;
    }

    private static Password validatePassword(Password password) {
        if (password == null) {
            throw validationError("password", "Password cannot be null");
        }
        return password;
    }

    private static Role validateRole(Role role) {
        if (role == null) {
            throw validationError("role", "Role cannot be null");
        }
        return role;
    }

    private static Instant validateTimestamp(String fieldName, Instant timestamp) {
        if (timestamp == null) {
            throw validationError(fieldName, fieldName + " cannot be null");
        }
        return timestamp;
    }

    private static ValidationException validationError(String fieldName, String message) {
        return ValidationException.of(
                CommonErrorType.VALIDATION_FAILED,
                Map.of(fieldName, message));
    }

    public UUID getId() {
        return id;
    }

    public Email getEmail() {
        return email;
    }

    public Password getPassword() {
        return password;
    }

    public Role getRole() {
        return role;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
