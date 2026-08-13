package com.devlab.pdf_wizard.adapter.out.persistence.pdf;

import com.devlab.pdf_wizard.domain.model.PdfDocument;

public final class PdfDocumentMapper {

    private PdfDocumentMapper() {
    }

    public static PdfDocumentEntity toEntity(PdfDocument document) {
        return new PdfDocumentEntity(
                document.getId(),
                document.getFileName(),
                document.getStoredFileName(),
                document.getContentType(),
                document.getSize(),
                document.getCreatedBy(),
                document.getCreatedAt(),
                document.getUpdatedAt());
    }

    public static PdfDocument toDomain(PdfDocumentEntity entity) {
        return PdfDocument.restore(
                entity.getId(),
                entity.getFileName(),
                entity.getStoredFileName(),
                entity.getContentType(),
                entity.getSize(),
                entity.getCreatedBy(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}
