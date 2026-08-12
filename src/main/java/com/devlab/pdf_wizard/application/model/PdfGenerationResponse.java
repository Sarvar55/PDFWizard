package com.devlab.pdf_wizard.application.model;

public record PdfGenerationResponse(
        String fileName,
        String contentType,
        byte[] content) {
}
