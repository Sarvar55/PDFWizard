package com.devlab.pdf_wizard.application.model;

import java.util.Map;

public record PdfGenerationRequest(
        String fileName,
        String title,
        Map<String, Object> data) {
}
