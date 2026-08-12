package com.devlab.pdf_wizard.domain;

import org.junit.jupiter.api.Test;

import com.devlab.pdf_wizard.domain.exception.ValidationException;
import com.devlab.pdf_wizard.domain.model.ContentType;
import com.devlab.pdf_wizard.domain.model.PdfDocument;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;

public class PdfDocumentTest {

    @Test
    @DisplayName("should create pdf document when all fields are valid")
    void shouldCreatePdfDocumentWhenAllFieldsAreValid() {
        PdfDocument pdfDocument = PdfDocument.create(
                "test.pdf",
                "documents/test.pdf",
                ContentType.PDF,
                100L,
                "sarvar");

        assertThat(pdfDocument.getId()).isNotNull();
        assertThat(pdfDocument.getFileName()).isEqualTo("test.pdf");
        assertThat(pdfDocument.getStoredFileName()).isEqualTo("documents/test.pdf");
        assertThat(pdfDocument.getContentType()).isEqualTo(ContentType.PDF);
        assertThat(pdfDocument.getSize()).isEqualTo(100L);
        assertThat(pdfDocument.getCreatedBy()).isEqualTo("sarvar");
        assertThat(pdfDocument.getCreatedAt()).isNotNull();
        assertThat(pdfDocument.getUpdatedAt()).isEqualTo(pdfDocument.getCreatedAt());
    }

    @Test
    @DisplayName("should throw exception when file name is null")
    void shouldThrowExceptionWhenFileNameIsNull() {

        assertThatThrownBy(() -> PdfDocument.create(
                null,
                "test.pdf",
                ContentType.PDF,
                100L,
                "sarvar"))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    @DisplayName("should throw exception when file name is empty")
    void shouldThrowExceptionWhenFileNameIsEmpty() {

        assertThatThrownBy(() -> PdfDocument.create(
                "",
                "test.pdf",
                ContentType.PDF,
                100L,
                "sarvar"))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    @DisplayName("should throw exception when file extension is not pdf")
    void shouldThrowExceptionWhenFileExtensionIsNotPdf() {
        assertThatThrownBy(() -> PdfDocument.create(
                "test.txt",
                "test.txt",
                ContentType.PDF,
                100L,
                "sarvar"))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    @DisplayName("should throw exception when size is not positive")
    void shouldThrowExceptionWhenSizeIsNotPositive() {
        assertThatThrownBy(() -> PdfDocument.create(
                "test.pdf",
                "test.pdf",
                ContentType.PDF,
                0L,
                "sarvar"))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    @DisplayName("should throw exception when creator is blank")
    void shouldThrowExceptionWhenCreatorIsBlank() {
        assertThatThrownBy(() -> PdfDocument.create(
                "test.pdf",
                "test.pdf",
                ContentType.PDF,
                100L,
                " "))
                .isInstanceOf(ValidationException.class);
    }
}
