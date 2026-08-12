package com.devlab.pdf_wizard.application.in;

import com.devlab.pdf_wizard.application.in.command.DeletePdfCommand;

public interface DeletePdfUseCase {

    void execute(DeletePdfCommand command);
}
