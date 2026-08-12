package com.devlab.pdf_wizard.application.in;

import com.devlab.pdf_wizard.application.in.command.SendPdfEmailCommand;

public interface SendPdfEmailUseCase {

    void execute(SendPdfEmailCommand command);
}
