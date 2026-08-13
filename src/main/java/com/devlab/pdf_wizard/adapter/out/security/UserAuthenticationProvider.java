package com.devlab.pdf_wizard.adapter.out.security;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.devlab.pdf_wizard.application.out.UserLoadPort;
import com.devlab.pdf_wizard.domain.model.Email;
import com.devlab.pdf_wizard.domain.model.User;
import com.devlab.pdf_wizard.domain.service.PasswordDomainService;

public class UserAuthenticationProvider implements AuthenticationProvider {

    private static final int MAX_PASSWORD_UTF8_BYTES = 72;

    private final UserLoadPort userLoadPort;
    private final PasswordDomainService passwordDomainService;

    public UserAuthenticationProvider(
            UserLoadPort userLoadPort,
            PasswordDomainService passwordDomainService) {
        this.userLoadPort = userLoadPort;
        this.passwordDomainService = passwordDomainService;
    }

    @Override
    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException {
        Email email = Email.of(authentication.getName());
        Object credentials = authentication.getCredentials();
        if (credentials == null) {
            throw new BadCredentialsException("Invalid credentials");
        }
        String plainPassword = credentials.toString();
        if (plainPassword.getBytes(StandardCharsets.UTF_8).length > MAX_PASSWORD_UTF8_BYTES) {
            throw new BadCredentialsException("Invalid credentials");
        }

        User user = userLoadPort.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        if (!user.isEnabled()) {
            throw new DisabledException("User account is disabled");
        }
        if (!user.getPassword().matches(plainPassword, passwordDomainService)) {
            throw new BadCredentialsException("Invalid credentials");
        }

        return new UsernamePasswordAuthenticationToken(
                user,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())));
    }

    @Override
    public boolean supports(Class<?> authenticationType) {
        return UsernamePasswordAuthenticationToken.class
                .isAssignableFrom(authenticationType);
    }
}
