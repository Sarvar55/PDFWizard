package com.devlab.pdf_wizard.config;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;

import com.devlab.pdf_wizard.adapter.out.security.JwtTokenProvider;
import com.devlab.pdf_wizard.adapter.out.security.UserAuthenticationProvider;
import com.devlab.pdf_wizard.application.AuthService;
import com.devlab.pdf_wizard.application.out.TokenProvider;
import com.devlab.pdf_wizard.application.out.UserLoadPort;
import com.devlab.pdf_wizard.application.out.UserSavePort;
import com.devlab.pdf_wizard.domain.service.PasswordDomainService;

@Configuration
public class AuthConfig {

    @Bean
    public JwtTokenProvider jwtTokenProvider(
            @Value("${pdf-wizard.auth.jwt.secret}") String secret,
            @Value("${pdf-wizard.auth.jwt.access-token-ttl}") Duration accessTokenTtl,
            @Value("${pdf-wizard.auth.jwt.issuer}") String issuer) {
        return new JwtTokenProvider(secret, accessTokenTtl, issuer);
    }

    @Bean
    public UserAuthenticationProvider userAuthenticationProvider(
            UserLoadPort userLoadPort,
            PasswordDomainService passwordDomainService) {
        return new UserAuthenticationProvider(userLoadPort, passwordDomainService);
    }

    @Bean
    public AuthenticationManager authenticationManager(
            UserAuthenticationProvider userAuthenticationProvider) {
        return new ProviderManager(userAuthenticationProvider);
    }

    @Bean
    public AuthService authService(
            UserLoadPort userLoadPort,
            UserSavePort userSavePort,
            PasswordDomainService passwordDomainService,
            AuthenticationManager authenticationManager,
            TokenProvider tokenProvider) {
        return new AuthService(
                userLoadPort,
                userSavePort,
                passwordDomainService,
                authenticationManager,
                tokenProvider);
    }
}
