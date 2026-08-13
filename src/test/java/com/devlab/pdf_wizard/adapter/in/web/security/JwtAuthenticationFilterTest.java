package com.devlab.pdf_wizard.adapter.in.web.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.devlab.pdf_wizard.application.model.AuthenticatedUser;
import com.devlab.pdf_wizard.application.out.TokenValidator;
import com.devlab.pdf_wizard.domain.model.Email;
import com.devlab.pdf_wizard.domain.model.Role;

class JwtAuthenticationFilterTest {

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldAuthenticateRequestWithValidBearerToken() throws Exception {
        AuthenticatedUser user = new AuthenticatedUser(
                UUID.randomUUID(),
                Email.of("user@example.com"),
                Role.USER);
        TokenValidator tokenValidator = token -> user;
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(tokenValidator);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer valid-token");

        filter.doFilter(
                request,
                new MockHttpServletResponse(),
                new MockFilterChain());

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication).isNotNull();
        assertThat(authentication.isAuthenticated()).isTrue();
        assertThat(authentication.getPrincipal()).isEqualTo(user);
        assertThat(authentication.getName()).isEqualTo("user@example.com");
        assertThat(authentication.getAuthorities())
                .extracting("authority")
                .containsExactly("ROLE_USER");
    }

    @Test
    void shouldKeepRequestAnonymousWhenTokenIsInvalid() throws Exception {
        TokenValidator tokenValidator = token -> {
            throw new IllegalArgumentException("Invalid token");
        };
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(tokenValidator);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer invalid-token");

        filter.doFilter(
                request,
                new MockHttpServletResponse(),
                new MockFilterChain());

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }
}
