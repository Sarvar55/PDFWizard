package com.devlab.pdf_wizard.application.in.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.ByteArrayInputStream;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.devlab.pdf_wizard.application.model.UploadedPdf;
import com.devlab.pdf_wizard.domain.exception.ValidationException;

class MergePdfCommandTest {

    @Test
    @DisplayName("should create merge command with two PDF files")
    void shouldCreateMergeCommandWithTwoPdfFiles() {
        List<UploadedPdf> files = List.of(pdf("first.pdf"), pdf("second.pdf"));

        MergePdfCommand command = MergePdfCommand.of("merged.pdf", files, "system");

        assertThat(command.outputFileName()).isEqualTo("merged.pdf");
        assertThat(command.files()).hasSize(2);
        assertThat(command.createdBy()).isEqualTo("system");
    }

    @Test
    @DisplayName("should reject merge command with fewer than two files")
    void shouldRejectMergeCommandWithFewerThanTwoFiles() {
        assertThatThrownBy(() -> MergePdfCommand.of(
                "merged.pdf",
                List.of(pdf("only.pdf")),
                "system"))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    @DisplayName("should reject uploaded file with non-PDF content type")
    void shouldRejectUploadedFileWithNonPdfContentType() {
        assertThatThrownBy(() -> UploadedPdf.of(
                "document.pdf",
                "text/plain",
                10,
                () -> new ByteArrayInputStream(new byte[] { 1 })))
                .isInstanceOf(ValidationException.class);
    }

    private UploadedPdf pdf(String fileName) {
        return UploadedPdf.of(
                fileName,
                "application/pdf",
                10,
                () -> new ByteArrayInputStream(new byte[] { 1 }));
    }
}
