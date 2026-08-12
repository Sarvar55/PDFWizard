package com.devlab.pdf_wizard.domain.exception;

import java.util.Map;
import java.util.UUID;

public class PdfDocumentNotFoundException extends BaseException {

    private PdfDocumentNotFoundException(UUID id) {
        super(
                CommonErrorType.RESOURCE_NOT_FOUND,
                null,
                Map.of("id", id));
    }

    public static PdfDocumentNotFoundException forId(UUID id) {
        return new PdfDocumentNotFoundException(id);
    }
}
