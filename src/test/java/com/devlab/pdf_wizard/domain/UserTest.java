package com.devlab.pdf_wizard.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

import com.devlab.pdf_wizard.domain.exception.ValidationException;
import com.devlab.pdf_wizard.domain.model.Email;
import com.devlab.pdf_wizard.domain.model.PasswordHash;
import com.devlab.pdf_wizard.domain.model.Role;
import com.devlab.pdf_wizard.domain.model.User;

class UserTest {

    @Test
    void shouldRegisterEnabledUser() {
        User user = User.register(
                Email.of("  User@Example.COM "),
                PasswordHash.of("stored-password-hash"));

        assertThat(user.getId()).isNotNull();
        assertThat(user.getEmail()).isEqualTo(Email.of("user@example.com"));
        assertThat(user.getRole()).isEqualTo(Role.USER);
        assertThat(user.isEnabled()).isTrue();
    }

    @Test
    void shouldRejectInvalidEmail() {
        assertThatThrownBy(() -> Email.of("invalid-email"))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void shouldNeverExposePasswordValuesThroughToString() {
        assertThat(PasswordHash.of("stored-password-hash").toString())
                .isEqualTo("[PROTECTED]");
    }
}
