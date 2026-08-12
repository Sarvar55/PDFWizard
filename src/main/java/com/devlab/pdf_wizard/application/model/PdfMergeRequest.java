package com.devlab.pdf_wizard.application.model;

import java.util.List;

public record PdfMergeRequest(
        String outputFileName,
        List<UploadedPdf> files) {
}
