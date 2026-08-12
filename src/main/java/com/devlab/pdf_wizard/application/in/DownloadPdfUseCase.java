package com.devlab.pdf_wizard.application.in;

import com.devlab.pdf_wizard.application.in.query.DownloadPdfQuery;
import com.devlab.pdf_wizard.application.model.PdfDownloadResult;

public interface DownloadPdfUseCase {

    PdfDownloadResult execute(DownloadPdfQuery query);
}
