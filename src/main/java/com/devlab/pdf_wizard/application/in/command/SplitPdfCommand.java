package com.devlab.pdf_wizard.application.in.command;

import java.util.Map;

import com.devlab.pdf_wizard.application.model.UploadedPdf;
import com.devlab.pdf_wizard.domain.exception.CommonErrorType;
import com.devlab.pdf_wizard.domain.exception.ValidationException;

public record SplitPdfCommand(
        String outputFileNamePrefix,
        UploadedPdf file,
        String createdBy) {

    public SplitPdfCommand {
        if (outputFileNamePrefix == null || outputFileNamePrefix.isBlank()) {
            throw validationError(
                    "outputFileNamePrefix",
                    "Output file name prefix cannot be blank");
        }
        if (outputFileNamePrefix.toLowerCase().endsWith(".pdf")) {
            throw validationError(
                    "outputFileNamePrefix",
                    "Output file name prefix must not contain the .pdf extension");
        }
        if (createdBy == null || createdBy.isBlank()) {
            throw validationError("createdBy", "Creator cannot be blank");
        }

        if (file == null) {
            throw validationError("file", "Uploaded PDF cannot be null");
        }
        outputFileNamePrefix = outputFileNamePrefix.trim();
        createdBy = createdBy.trim();
    }

    public static SplitPdfCommand of(
            String outputFileNamePrefix,
            UploadedPdf file,
            String createdBy) {
        return new SplitPdfCommand(outputFileNamePrefix, file, createdBy);
    }

    private static ValidationException validationError(String field, String message) {
        return ValidationException.of(
                CommonErrorType.VALIDATION_FAILED,
                Map.of(field, message));
    }
}
