package com.devlab.pdf_wizard.adapter.out.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import com.devlab.pdf_wizard.application.out.UserLoadPort;
import com.devlab.pdf_wizard.domain.model.Email;
import com.devlab.pdf_wizard.domain.model.Password;
import com.devlab.pdf_wizard.domain.model.User;
import com.devlab.pdf_wizard.domain.service.PasswordDomainService;

class UserAuthenticationProviderTest {

    private final PasswordDomainService passwordService =
            new BCryptPasswordDomainService();

    @Test
    void shouldReturnAuthenticatedTokenForCorrectCredentials() {
        User user = userWithPassword("strong-password");
        UserAuthenticationProvider provider = providerReturning(user);
        Authentication request = UsernamePasswordAuthenticationToken.unauthenticated(
                "user@example.com",
                "strong-password");

        Authentication result = provider.authenticate(request);

        assertThat(result.isAuthenticated()).isTrue();
        assertThat(result.getPrincipal()).isSameAs(user);
        assertThat(result.getCredentials()).isNull();
        assertThat(result.getAuthorities())
                .extracting("authority")
                .containsExactly("ROLE_USER");
    }

    @Test
    void shouldThrowBadCredentialsForWrongPassword() {
        UserAuthenticationProvider provider = providerReturning(
                userWithPassword("strong-password"));
        Authentication request = UsernamePasswordAuthenticationToken.unauthenticated(
                "user@example.com",
                "wrong-password");

        assertThatThrownBy(() -> provider.authenticate(request))
                .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    void shouldThrowDisabledExceptionForDisabledUser() {
        User user = userWithPassword("strong-password");
        user.disable();
        UserAuthenticationProvider provider = providerReturning(user);
        Authentication request = UsernamePasswordAuthenticationToken.unauthenticated(
                "user@example.com",
                "strong-password");

        assertThatThrownBy(() -> provider.authenticate(request))
                .isInstanceOf(DisabledException.class);
    }

    @Test
    void shouldSupportUsernamePasswordAuthenticationToken() {
        UserAuthenticationProvider provider = new UserAuthenticationProvider(
                email -> Optional.empty(),
                passwordService);

        assertThat(provider.supports(UsernamePasswordAuthenticationToken.class)).isTrue();
    }

    private UserAuthenticationProvider providerReturning(User user) {
        UserLoadPort userLoadPort = email -> Optional.of(user);
        return new UserAuthenticationProvider(userLoadPort, passwordService);
    }

    private User userWithPassword(String plainPassword) {
        return User.register(
                Email.of("user@example.com"),
                Password.fromPlainText(plainPassword, passwordService));
    }
}
