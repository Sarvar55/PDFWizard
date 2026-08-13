package com.devlab.pdf_wizard.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

import com.devlab.pdf_wizard.adapter.out.security.BCryptPasswordDomainService;
import com.devlab.pdf_wizard.domain.model.Password;
import com.devlab.pdf_wizard.domain.exception.InvalidPasswordException;
import com.devlab.pdf_wizard.domain.service.PasswordDomainService;

class PasswordDomainServiceTest {

    private final PasswordDomainService passwordService =
            new BCryptPasswordDomainService();

    @Test
    void shouldHashAndMatchPassword() {
        String plainPassword = "strong-password";

        Password password = Password.fromPlainText(plainPassword, passwordService);

        assertThat(password.value()).isNotEqualTo(plainPassword);
        assertThat(password.matches(plainPassword, passwordService)).isTrue();
        assertThat(password.matches("wrong-password", passwordService)).isFalse();
    }

    @Test
    void shouldRejectPasswordLongerThanBcryptUtf8Limit() {
        String multiBytePassword = "ş".repeat(37);

        assertThatThrownBy(() -> Password.fromPlainText(
                multiBytePassword,
                passwordService))
                .isInstanceOf(InvalidPasswordException.class)
                .hasMessage("Password is invalid");
    }
}
