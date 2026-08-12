package com.devlab.pdf_wizard.adapter.in.web.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.devlab.pdf_wizard.domain.model.ContentType;
import com.devlab.pdf_wizard.domain.model.PdfDocument;

class PdfMetadataResponseTest {

    @Test
    @DisplayName("should map PDF document to metadata response")
    void shouldMapPdfDocumentToMetadataResponse() {
        PdfDocument document = PdfDocument.create(
                "invoice.pdf",
                "documents/invoice.pdf",
                ContentType.PDF,
                256L,
                "sarvar");

        PdfMetadataResponse response = PdfMetadataResponse.from(document);

        assertThat(response.id()).isEqualTo(document.getId());
        assertThat(response.fileName()).isEqualTo(document.getFileName());
        assertThat(response.storedFileName()).isEqualTo(document.getStoredFileName());
        assertThat(response.contentType()).isEqualTo("PDF");
        assertThat(response.size()).isEqualTo(document.getSize());
        assertThat(response.createdBy()).isEqualTo(document.getCreatedBy());
        assertThat(response.createdAt()).isEqualTo(document.getCreatedAt());
        assertThat(response.updatedAt()).isEqualTo(document.getUpdatedAt());
    }
}
