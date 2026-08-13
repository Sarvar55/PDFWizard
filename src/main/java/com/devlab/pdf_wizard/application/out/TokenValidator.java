package com.devlab.pdf_wizard.application.out;

import com.devlab.pdf_wizard.application.model.AuthenticatedUser;

public interface TokenValidator {

    AuthenticatedUser validate(String token);
}
