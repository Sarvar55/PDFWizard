package com.devlab.pdf_wizard.application.out;

import com.devlab.pdf_wizard.domain.model.PdfDocument;

public interface PdfSavePort {
    PdfDocument save(PdfDocument pdfDocument);
}
