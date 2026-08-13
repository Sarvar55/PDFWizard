package com.devlab.pdf_wizard.application.out;

import com.devlab.pdf_wizard.application.model.AccessToken;
import com.devlab.pdf_wizard.domain.model.User;

public interface TokenProvider {

    AccessToken generate(User user);
}
