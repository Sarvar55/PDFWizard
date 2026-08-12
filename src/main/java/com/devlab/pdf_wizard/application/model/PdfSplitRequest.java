package com.devlab.pdf_wizard.application.model;

public record PdfSplitRequest(
        String outputFileNamePrefix,
        UploadedPdf file) {
}
