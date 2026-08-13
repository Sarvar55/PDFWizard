package com.devlab.pdf_wizard.domain.service;

public interface PasswordDomainService {

    String hash(String plainPassword);

    boolean matches(String rawPassword, String encodedPassword);
}
