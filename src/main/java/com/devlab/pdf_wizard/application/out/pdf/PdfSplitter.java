package com.devlab.pdf_wizard.application.out.pdf;

import java.util.List;

import com.devlab.pdf_wizard.application.model.PdfGenerationResponse;
import com.devlab.pdf_wizard.application.model.PdfSplitRequest;

public interface PdfSplitter {

    List<PdfGenerationResponse> split(PdfSplitRequest request);
}
