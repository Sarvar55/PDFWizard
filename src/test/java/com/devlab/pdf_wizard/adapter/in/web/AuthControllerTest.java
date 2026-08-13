package com.devlab.pdf_wizard.adapter.in.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.concurrent.atomic.AtomicReference;
import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.devlab.pdf_wizard.adapter.in.web.exception.GlobalExceptionHandler;
import com.devlab.pdf_wizard.application.in.RegisterUserUseCase;
import com.devlab.pdf_wizard.application.in.LoginUseCase;
import com.devlab.pdf_wizard.application.in.command.LoginCommand;
import com.devlab.pdf_wizard.application.in.command.RegisterUserCommand;
import com.devlab.pdf_wizard.application.model.AccessToken;
import com.devlab.pdf_wizard.domain.model.Email;
import com.devlab.pdf_wizard.domain.model.Password;
import com.devlab.pdf_wizard.domain.model.User;

class AuthControllerTest {

    private MockMvc mockMvc;
    private AtomicReference<RegisterUserCommand> receivedCommand;
    private AtomicReference<LoginCommand> loginCommand;

    @BeforeEach
    void setUp() {
        receivedCommand = new AtomicReference<>();
        RegisterUserUseCase registerUserUseCase = command -> {
            receivedCommand.set(command);
            return User.register(
                    command.email(),
                    Password.fromHashed("encoded-password"));
        };
        loginCommand = new AtomicReference<>();
        LoginUseCase loginUseCase = command -> {
            loginCommand.set(command);
            return new AccessToken(
                    "signed.jwt.token",
                    Instant.parse("2026-08-12T12:00:00Z"));
        };

        mockMvc = MockMvcBuilders
                .standaloneSetup(new AuthController(registerUserUseCase, loginUseCase))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void shouldRegisterUserAndReturnSafeResponse() throws Exception {
        String requestBody = """
                {
                  "email": "User@Example.COM",
                  "password": "strong-password"
                }
                """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.email").value("user@example.com"))
                .andExpect(jsonPath("$.role").value("USER"))
                .andExpect(jsonPath("$.enabled").value(true))
                .andExpect(jsonPath("$.password").doesNotExist());

        assertThat(receivedCommand.get().email())
                .isEqualTo(Email.of("user@example.com"));
        assertThat(receivedCommand.get().password())
                .isEqualTo("strong-password");
    }

    @Test
    void shouldLoginAndReturnBearerToken() throws Exception {
        String requestBody = """
                {
                  "email": "user@example.com",
                  "password": "strong-password"
                }
                """;

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("signed.jwt.token"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.expiresAt").value("2026-08-12T12:00:00Z"));

        assertThat(loginCommand.get().email())
                .isEqualTo(Email.of("user@example.com"));
    }

    @Test
    void shouldReturnStandardValidationErrorForInvalidRegistration() throws Exception {
        String requestBody = """
                {
                  "email": "not-an-email",
                  "password": "short"
                }
                """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.validationErrors.email").value(
                        "Email format is invalid"))
                .andExpect(jsonPath("$.validationErrors.password").value(
                        "Password must contain between 8 and 72 characters"));

        assertThat(receivedCommand.get()).isNull();
    }
}
