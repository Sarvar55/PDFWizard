package com.devlab.pdf_wizard.application.in;

import java.util.List;

import com.devlab.pdf_wizard.application.in.command.SplitPdfCommand;
import com.devlab.pdf_wizard.domain.model.PdfDocument;

public interface SplitPdfUseCase {

    List<PdfDocument> execute(SplitPdfCommand command);
}
