package com.devlab.pdf_wizard.adapter.in.web.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import com.devlab.pdf_wizard.domain.model.PdfDocument;

public record PdfMetadataResponse(
        UUID id,
        String fileName,
        String storedFileName,
        String contentType,
        Long size,
        String createdBy,
        Instant createdAt,
        Instant updatedAt) {

    public static PdfMetadataResponse from(PdfDocument document) {
        Objects.requireNonNull(document, "PDF document cannot be null");

        return new PdfMetadataResponse(
                document.getId(),
                document.getFileName(),
                document.getStoredFileName(),
                document.getContentType().name(),
                document.getSize(),
                document.getCreatedBy(),
                document.getCreatedAt(),
                document.getUpdatedAt());
    }
}
