package com.devlab.pdf_wizard.application.in;

import com.devlab.pdf_wizard.application.in.command.LoginCommand;
import com.devlab.pdf_wizard.application.model.AccessToken;

public interface LoginUseCase {

    AccessToken login(LoginCommand command);
}
