package com.devlab.pdf_wizard.application.in.query;

import java.util.Map;
import java.util.UUID;

import com.devlab.pdf_wizard.domain.exception.CommonErrorType;
import com.devlab.pdf_wizard.domain.exception.ValidationException;

public record GetPdfMetadataQuery(UUID id) {

    public GetPdfMetadataQuery {
        if (id == null) {
            throw ValidationException.of(
                    CommonErrorType.VALIDATION_FAILED,
                    Map.of("id", "Document id cannot be null"));
        }
    }

    public static GetPdfMetadataQuery of(UUID id) {
        return new GetPdfMetadataQuery(id);
    }
}
