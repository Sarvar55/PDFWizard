package com.devlab.pdf_wizard.application.in;

import com.devlab.pdf_wizard.application.in.command.RegisterUserCommand;
import com.devlab.pdf_wizard.domain.model.User;

public interface RegisterUserUseCase {

    User register(RegisterUserCommand command);
}
