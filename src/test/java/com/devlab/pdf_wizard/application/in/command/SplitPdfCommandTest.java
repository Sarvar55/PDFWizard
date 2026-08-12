package com.devlab.pdf_wizard.application.in.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.ByteArrayInputStream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.devlab.pdf_wizard.application.model.UploadedPdf;
import com.devlab.pdf_wizard.domain.exception.ValidationException;

class SplitPdfCommandTest {

    @Test
    @DisplayName("should create split command with valid values")
    void shouldCreateSplitCommandWithValidValues() {
        UploadedPdf file = uploadedPdf();

        SplitPdfCommand command = SplitPdfCommand.of("contract", file, "system");

        assertThat(command.outputFileNamePrefix()).isEqualTo("contract");
        assertThat(command.file()).isSameAs(file);
        assertThat(command.createdBy()).isEqualTo("system");
    }

    @Test
    @DisplayName("should reject output prefix containing PDF extension")
    void shouldRejectOutputPrefixContainingPdfExtension() {
        assertThatThrownBy(() -> SplitPdfCommand.of(
                "contract.pdf",
                uploadedPdf(),
                "system"))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    @DisplayName("should reject null uploaded PDF")
    void shouldRejectNullUploadedPdf() {
        assertThatThrownBy(() -> SplitPdfCommand.of("contract", null, "system"))
                .isInstanceOf(ValidationException.class);
    }

    private UploadedPdf uploadedPdf() {
        return UploadedPdf.of(
                "contract.pdf",
                "application/pdf",
                10,
                () -> new ByteArrayInputStream(new byte[] { 1 }));
    }
}
