package com.devlab.pdf_wizard.application.in;

import com.devlab.pdf_wizard.application.in.query.GetPdfMetadataQuery;
import com.devlab.pdf_wizard.domain.model.PdfDocument;

public interface GetPdfMetadataUseCase {

    PdfDocument execute(GetPdfMetadataQuery query);
}
