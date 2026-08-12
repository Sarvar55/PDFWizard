package com.devlab.pdf_wizard.application.out.email;

import com.devlab.pdf_wizard.application.in.command.SendPdfEmailCommand;

public interface PdfEmailTaskDispatcher {

    void dispatch(SendPdfEmailCommand command);
}
