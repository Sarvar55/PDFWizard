package com.devlab.pdf_wizard.application.model;

import java.util.Map;
import java.util.Objects;

import com.devlab.pdf_wizard.domain.exception.CommonErrorType;
import com.devlab.pdf_wizard.domain.exception.ValidationException;
import com.devlab.pdf_wizard.domain.model.ContentType;

public record UploadedPdf(
        String fileName,
        String contentType,
        long size,
        PdfContentSource contentSource) {

    public UploadedPdf {
        if (fileName == null || fileName.isBlank()) {
            throw validationError("fileName", "Uploaded file name cannot be blank");
        }
        if (!fileName.toLowerCase().endsWith(".pdf")) {
            throw validationError("fileName", "Uploaded file must have a .pdf extension");
        }
        if (contentType == null || !ContentType.PDF.getMimeTypes().contains(contentType)) {
            throw validationError("contentType", "Uploaded file must have a PDF content type");
        }
        if (size <= 0) {
            throw validationError("size", "Uploaded PDF cannot be empty");
        }

        contentSource = Objects.requireNonNull(contentSource, "PDF content source cannot be null");
        fileName = fileName.trim();
    }

    public static UploadedPdf of(
            String fileName,
            String contentType,
            long size,
            PdfContentSource contentSource) {
        return new UploadedPdf(fileName, contentType, size, contentSource);
    }

    private static ValidationException validationError(String field, String message) {
        return ValidationException.of(
                CommonErrorType.VALIDATION_FAILED,
                Map.of(field, message));
    }
}
