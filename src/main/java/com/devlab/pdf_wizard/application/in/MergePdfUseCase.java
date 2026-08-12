package com.devlab.pdf_wizard.application.in;

import com.devlab.pdf_wizard.application.in.command.MergePdfCommand;
import com.devlab.pdf_wizard.domain.model.PdfDocument;

public interface MergePdfUseCase {

    PdfDocument execute(MergePdfCommand command);
}
