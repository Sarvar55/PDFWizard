package com.devlab.pdf_wizard.domain.service;

import com.devlab.pdf_wizard.domain.model.Password;
import com.devlab.pdf_wizard.domain.model.PasswordHash;

public interface PasswordDomainService {

    PasswordHash hash(Password password);

    boolean matches(Password password, PasswordHash passwordHash);
}
