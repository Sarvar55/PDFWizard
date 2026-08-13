package com.devlab.pdf_wizard.adapter.out.security;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import javax.crypto.SecretKey;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import com.devlab.pdf_wizard.application.model.AccessToken;
import com.devlab.pdf_wizard.application.model.AuthenticatedUser;
import com.devlab.pdf_wizard.application.out.TokenProvider;
import com.devlab.pdf_wizard.application.out.TokenValidator;
import com.devlab.pdf_wizard.domain.model.Email;
import com.devlab.pdf_wizard.domain.model.Role;
import com.devlab.pdf_wizard.domain.model.User;

public class JwtTokenProvider implements TokenProvider, TokenValidator {

    private final SecretKey signingKey;
    private final Duration accessTokenTtl;
    private final String issuer;

    public JwtTokenProvider(String base64Secret, Duration accessTokenTtl, String issuer) {
        if (base64Secret == null || base64Secret.isBlank()) {
            throw new IllegalArgumentException("JWT secret cannot be null or empty");
        }
        if (accessTokenTtl == null || accessTokenTtl.isZero() || accessTokenTtl.isNegative()) {
            throw new IllegalArgumentException("Access token TTL must be positive");
        }
        if (issuer == null || issuer.isBlank()) {
            throw new IllegalArgumentException("JWT issuer cannot be null or empty");
        }

        this.signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(base64Secret));
        this.accessTokenTtl = accessTokenTtl;
        this.issuer = issuer;
    }

    @Override
    public AccessToken generate(User user) {
        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plus(accessTokenTtl);

        String token = Jwts.builder()
                .issuer(issuer)
                .subject(user.getId().toString())
                .claim("email", user.getEmail().value())
                .claim("role", user.getRole().name())
                .issuedAt(Date.from(issuedAt))
                .expiration(Date.from(expiresAt))
                .signWith(signingKey)
                .compact();

        return new AccessToken(token, expiresAt);
    }

    @Override
    public AuthenticatedUser validate(String token) {
        var claims = Jwts.parser()
                .verifyWith(signingKey)
                .requireIssuer(issuer)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return new AuthenticatedUser(
                UUID.fromString(claims.getSubject()),
                Email.of(claims.get("email", String.class)),
                Role.valueOf(claims.get("role", String.class)));
    }
}
