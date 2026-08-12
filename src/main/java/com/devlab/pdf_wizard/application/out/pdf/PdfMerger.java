package com.devlab.pdf_wizard.application.out.pdf;

import com.devlab.pdf_wizard.application.model.PdfGenerationResponse;
import com.devlab.pdf_wizard.application.model.PdfMergeRequest;

public interface PdfMerger {

    PdfGenerationResponse merge(PdfMergeRequest request);
}
