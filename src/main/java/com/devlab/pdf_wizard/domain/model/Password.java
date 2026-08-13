package com.devlab.pdf_wizard.domain.model;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

import com.devlab.pdf_wizard.domain.exception.InvalidPasswordException;
import com.devlab.pdf_wizard.domain.service.PasswordDomainService;

public final class Password {

    private static final int MIN_LENGTH = 8;
    private static final int MAX_UTF8_BYTES = 72;

    private final String hashedPassword;

    private Password(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public static Password fromPlainText(
            String plainPassword,
            PasswordDomainService passwordDomainService) {
        Objects.requireNonNull(
                passwordDomainService,
                "Password domain service cannot be null");

        if (plainPassword == null) {
            throw new InvalidPasswordException("Password cannot be null");
        }
        if (plainPassword.isBlank()) {
            throw new InvalidPasswordException("Password cannot be blank");
        }
        if (plainPassword.length() < MIN_LENGTH) {
            throw new InvalidPasswordException(
                    "Password must be at least 8 characters");
        }
        if (plainPassword.getBytes(StandardCharsets.UTF_8).length > MAX_UTF8_BYTES) {
            throw new InvalidPasswordException(
                    "Password cannot exceed 72 UTF-8 bytes");
        }

        return new Password(passwordDomainService.hash(plainPassword));
    }

    public boolean matches(
            String plainPassword,
            PasswordDomainService passwordDomainService) {
        if (plainPassword == null) {
            return false;
        }

        Objects.requireNonNull(
                passwordDomainService,
                "Password domain service cannot be null");

        return passwordDomainService.matches(plainPassword, hashedPassword);
    }

    public static Password fromHashed(String hashedPassword) {
        if (hashedPassword == null || hashedPassword.isBlank()) {
            throw new InvalidPasswordException("Hashed password cannot be blank");
        }

        return new Password(hashedPassword);
    }

    public String value() {
        return hashedPassword;
    }

    @Override
    public String toString() {
        return "Password[PROTECTED]";
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof Password password)) {
            return false;
        }
        return hashedPassword.equals(password.hashedPassword);
    }

    @Override
    public int hashCode() {
        return hashedPassword.hashCode();
    }
}
