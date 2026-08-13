package com.devlab.pdf_wizard.adapter.out.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.devlab.pdf_wizard.domain.service.PasswordDomainService;

@Component
public class BCryptPasswordDomainService implements PasswordDomainService {

    private final BCryptPasswordEncoder passwordEncoder;

    public BCryptPasswordDomainService() {
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Override
    public String hash(String plainPassword) {
        return passwordEncoder.encode(plainPassword);
    }

    @Override
    public boolean matches(String plainPassword, String hashedPassword) {
        return passwordEncoder.matches(plainPassword, hashedPassword);
    }
}
