package com.devlab.pdf_wizard.application.out;

import java.util.Optional;

import com.devlab.pdf_wizard.domain.model.Email;
import com.devlab.pdf_wizard.domain.model.User;

public interface UserLoadPort {

    Optional<User> findByEmail(Email email);
}
