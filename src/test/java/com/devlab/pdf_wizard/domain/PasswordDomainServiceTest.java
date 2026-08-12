package com.devlab.pdf_wizard.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.devlab.pdf_wizard.adapter.out.security.BCryptPasswordDomainService;
import com.devlab.pdf_wizard.domain.model.Password;
import com.devlab.pdf_wizard.domain.model.PasswordHash;
import com.devlab.pdf_wizard.domain.service.PasswordDomainService;

class PasswordDomainServiceTest {

    private final PasswordDomainService passwordService =
            new BCryptPasswordDomainService();

    @Test
    void shouldHashAndMatchPassword() {
        Password password = Password.of("strong-password");

        PasswordHash hash = passwordService.hash(password);

        assertThat(hash.value()).isNotEqualTo(password.value());
        assertThat(passwordService.matches(password, hash)).isTrue();
        assertThat(passwordService.matches(Password.of("wrong-password"), hash)).isFalse();
    }
}
