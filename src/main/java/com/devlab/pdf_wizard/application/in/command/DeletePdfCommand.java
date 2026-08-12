package com.devlab.pdf_wizard.application.in.command;

import java.util.Map;
import java.util.UUID;

import com.devlab.pdf_wizard.domain.exception.CommonErrorType;
import com.devlab.pdf_wizard.domain.exception.ValidationException;

public record DeletePdfCommand(UUID id) {

    public DeletePdfCommand {
        if (id == null) {
            throw ValidationException.of(
                    CommonErrorType.VALIDATION_FAILED,
                    Map.of("id", "Document id cannot be null"));
        }
    }

    public static DeletePdfCommand of(UUID id) {
        return new DeletePdfCommand(id);
    }
}
