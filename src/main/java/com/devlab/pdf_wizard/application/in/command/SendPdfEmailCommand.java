package com.devlab.pdf_wizard.application.in.command;

import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

import com.devlab.pdf_wizard.domain.exception.CommonErrorType;
import com.devlab.pdf_wizard.domain.exception.ValidationException;

public record SendPdfEmailCommand(UUID documentId, String recipient) {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$",
            Pattern.CASE_INSENSITIVE);

    public SendPdfEmailCommand {
        if (documentId == null) {
            throw validationError("documentId", "Document id cannot be null");
        }
        if (recipient == null || recipient.isBlank()) {
            throw validationError("recipient", "Recipient email cannot be blank");
        }

        recipient = recipient.trim();
        if (!EMAIL_PATTERN.matcher(recipient).matches()) {
            throw validationError("recipient", "Recipient email is invalid");
        }
    }

    public static SendPdfEmailCommand of(UUID documentId, String recipient) {
        return new SendPdfEmailCommand(documentId, recipient);
    }

    private static ValidationException validationError(String field, String message) {
        return ValidationException.of(
                CommonErrorType.VALIDATION_FAILED,
                Map.of(field, message));
    }
}
