package com.devlab.pdf_wizard.application.out;

import java.util.Optional;
import java.util.UUID;

import com.devlab.pdf_wizard.domain.model.PdfDocument;

public interface PdfLoadPort {

    Optional<PdfDocument> findById(UUID id);
}
