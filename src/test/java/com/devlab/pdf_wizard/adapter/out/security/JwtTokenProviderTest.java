package com.devlab.pdf_wizard.adapter.out.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.time.Instant;

import javax.crypto.SecretKey;

import org.junit.jupiter.api.Test;

import com.devlab.pdf_wizard.application.model.AccessToken;
import com.devlab.pdf_wizard.application.model.AuthenticatedUser;
import com.devlab.pdf_wizard.domain.model.Email;
import com.devlab.pdf_wizard.domain.model.Password;
import com.devlab.pdf_wizard.domain.model.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

class JwtTokenProviderTest {

    private static final String SECRET =
            "cGRmd2l6YXJkLXRlc3Qtand0LXNpZ25pbmctc2VjcmV0LTIwMjY=";

    @Test
    void shouldGenerateSignedTokenWithUserClaims() {
        User user = User.register(
                Email.of("user@example.com"),
                Password.fromHashed("encoded-password"));
        JwtTokenProvider tokenProvider = new JwtTokenProvider(
                SECRET,
                Duration.ofMinutes(15),
                "pdf-wizard");

        AccessToken accessToken = tokenProvider.generate(user);
        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET));
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(accessToken.value())
                .getPayload();

        assertThat(claims.getSubject()).isEqualTo(user.getId().toString());
        assertThat(claims.getIssuer()).isEqualTo("pdf-wizard");
        assertThat(claims.get("email", String.class)).isEqualTo("user@example.com");
        assertThat(claims.get("role", String.class)).isEqualTo("USER");
        assertThat(accessToken.expiresAt())
                .isAfter(Instant.now().plus(Duration.ofMinutes(14)));

        AuthenticatedUser authenticatedUser = tokenProvider.validate(accessToken.value());
        assertThat(authenticatedUser.id()).isEqualTo(user.getId());
        assertThat(authenticatedUser.email()).isEqualTo(user.getEmail());
        assertThat(authenticatedUser.role()).isEqualTo(user.getRole());
    }
}
