package com.devlab.pdf_wizard.adapter.out.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.devlab.pdf_wizard.domain.model.Password;
import com.devlab.pdf_wizard.domain.model.PasswordHash;
import com.devlab.pdf_wizard.domain.service.PasswordDomainService;

public class BCryptPasswordDomainService implements PasswordDomainService {

    private final BCryptPasswordEncoder passwordEncoder;

    public BCryptPasswordDomainService() {
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Override
    public PasswordHash hash(Password password) {
        return PasswordHash.of(passwordEncoder.encode(password.value()));
    }

    @Override
    public boolean matches(Password password, PasswordHash passwordHash) {
        return passwordEncoder.matches(password.value(), passwordHash.value());
    }
}
