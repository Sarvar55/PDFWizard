package com.devlab.pdf_wizard.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Optional;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import com.devlab.pdf_wizard.adapter.out.security.BCryptPasswordDomainService;
import com.devlab.pdf_wizard.application.in.command.RegisterUserCommand;
import com.devlab.pdf_wizard.application.in.command.LoginCommand;
import com.devlab.pdf_wizard.application.model.AccessToken;
import com.devlab.pdf_wizard.application.out.TokenProvider;
import com.devlab.pdf_wizard.application.out.UserLoadPort;
import com.devlab.pdf_wizard.application.out.UserSavePort;
import com.devlab.pdf_wizard.domain.exception.UserAlreadyExistsException;
import com.devlab.pdf_wizard.domain.model.Email;
import com.devlab.pdf_wizard.domain.model.Password;
import com.devlab.pdf_wizard.domain.model.User;
import com.devlab.pdf_wizard.domain.service.PasswordDomainService;

class AuthServiceTest {

    private final PasswordDomainService passwordDomainService =
            new BCryptPasswordDomainService();

    @Test
    void shouldHashPasswordAndSaveUser() {
        AtomicReference<User> savedUser = new AtomicReference<>();
        UserLoadPort loadPort = email -> Optional.empty();
        UserSavePort savePort = user -> {
            savedUser.set(user);
            return user;
        };
        AuthService authService = new AuthService(
                loadPort,
                savePort,
                passwordDomainService,
                authentication -> {
                    throw new UnsupportedOperationException();
                },
                user -> {
                    throw new UnsupportedOperationException();
                });
        RegisterUserCommand command = RegisterUserCommand.of(
                "user@example.com",
                "strong-password");

        User result = authService.register(command);

        assertThat(result).isSameAs(savedUser.get());
        assertThat(result.getEmail()).isEqualTo(Email.of("user@example.com"));
        assertThat(result.getPassword().value()).isNotEqualTo("strong-password");
        assertThat(result.getPassword().matches(
                "strong-password",
                passwordDomainService)).isTrue();
    }

    @Test
    void shouldRejectAlreadyRegisteredEmail() {
        User existingUser = User.register(
                Email.of("user@example.com"),
                Password.fromHashed("stored-password"));
        UserLoadPort loadPort = email -> Optional.of(existingUser);
        UserSavePort savePort = user -> user;
        AuthService authService = new AuthService(
                loadPort,
                savePort,
                passwordDomainService,
                authentication -> new UsernamePasswordAuthenticationToken(
                        existingUser,
                        null,
                        List.of()),
                user -> new AccessToken("token", Instant.now().plusSeconds(900)));

        assertThatThrownBy(() -> authService.register(
                RegisterUserCommand.of("user@example.com", "strong-password")))
                .isInstanceOf(UserAlreadyExistsException.class);
    }

    @Test
    void shouldAuthenticateAndGenerateAccessTokenOnLogin() {
        User user = User.register(
                Email.of("user@example.com"),
                Password.fromHashed("encoded-password"));
        AuthenticationManager authenticationManager = authentication ->
                new UsernamePasswordAuthenticationToken(user, null, List.of());
        AccessToken expectedToken = new AccessToken(
                "signed.jwt.token",
                Instant.now().plusSeconds(900));
        TokenProvider tokenProvider = authenticatedUser -> expectedToken;
        AuthService authService = new AuthService(
                email -> Optional.empty(),
                savedUser -> savedUser,
                passwordDomainService,
                authenticationManager,
                tokenProvider);

        AccessToken result = authService.login(
                LoginCommand.of("user@example.com", "strong-password"));

        assertThat(result).isEqualTo(expectedToken);
    }
}
