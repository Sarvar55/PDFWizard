package com.devlab.pdf_wizard.domain.model;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import com.devlab.pdf_wizard.domain.exception.CommonErrorType;
import com.devlab.pdf_wizard.domain.exception.ValidationException;

public class User {

    private final UUID id;
    private final Email email;
    private PasswordHash passwordHash;
    private final Role role;
    private boolean enabled;
    private final Instant createdAt;
    private Instant updatedAt;

    private User(UUID id, Email email, PasswordHash passwordHash, Role role,
            boolean enabled, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.enabled = enabled;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static User register(Email email, PasswordHash passwordHash) {
        Instant now = Instant.now();

        return new User(
                UUID.randomUUID(),
                validateEmail(email),
                validatePasswordHash(passwordHash),
                Role.USER,
                true,
                now,
                now);
    }

    public static User restore(UUID id, Email email, PasswordHash passwordHash,
            Role role, boolean enabled, Instant createdAt, Instant updatedAt) {
        return new User(
                validateId(id),
                validateEmail(email),
                validatePasswordHash(passwordHash),
                validateRole(role),
                enabled,
                validateTimestamp("createdAt", createdAt),
                validateTimestamp("updatedAt", updatedAt));
    }

    public void changePassword(PasswordHash newPasswordHash) {
        this.passwordHash = validatePasswordHash(newPasswordHash);
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

    private static PasswordHash validatePasswordHash(PasswordHash passwordHash) {
        if (passwordHash == null) {
            throw validationError("passwordHash", "Password hash cannot be null");
        }
        return passwordHash;
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

    public PasswordHash getPasswordHash() {
        return passwordHash;
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
