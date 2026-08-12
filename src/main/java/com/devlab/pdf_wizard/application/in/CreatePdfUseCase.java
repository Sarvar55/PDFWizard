package com.devlab.pdf_wizard.application.in;

import com.devlab.pdf_wizard.application.in.command.CreatePdfCommand;
import com.devlab.pdf_wizard.domain.model.PdfDocument;

public interface CreatePdfUseCase {

    PdfDocument execute(CreatePdfCommand command);
}
