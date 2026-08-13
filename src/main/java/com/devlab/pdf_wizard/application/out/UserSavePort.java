package com.devlab.pdf_wizard.application.out;

import com.devlab.pdf_wizard.domain.model.User;

public interface UserSavePort {

    User save(User user);
}
