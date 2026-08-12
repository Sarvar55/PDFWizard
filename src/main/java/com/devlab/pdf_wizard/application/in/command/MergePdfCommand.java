package com.devlab.pdf_wizard.application.in.command;

import java.util.List;
import java.util.Map;

import com.devlab.pdf_wizard.application.model.UploadedPdf;
import com.devlab.pdf_wizard.domain.exception.CommonErrorType;
import com.devlab.pdf_wizard.domain.exception.ValidationException;

public record MergePdfCommand(
        String outputFileName,
        List<UploadedPdf> files,
        String createdBy) {

    public MergePdfCommand {
        if (outputFileName == null || outputFileName.isBlank()) {
            throw validationError("outputFileName", "Output file name cannot be blank");
        }
        if (!outputFileName.toLowerCase().endsWith(".pdf")) {
            throw validationError("outputFileName", "Output file name must have a .pdf extension");
        }
        if (files == null || files.size() < 2) {
            throw validationError("files", "At least two PDF files are required");
        }
        if (createdBy == null || createdBy.isBlank()) {
            throw validationError("createdBy", "Creator cannot be blank");
        }

        outputFileName = outputFileName.trim();
        files = List.copyOf(files);
        createdBy = createdBy.trim();
    }

    public static MergePdfCommand of(
            String outputFileName,
            List<UploadedPdf> files,
            String createdBy) {
        return new MergePdfCommand(outputFileName, files, createdBy);
    }

    private static ValidationException validationError(String field, String message) {
        return ValidationException.of(
                CommonErrorType.VALIDATION_FAILED,
                Map.of(field, message));
    }
}
