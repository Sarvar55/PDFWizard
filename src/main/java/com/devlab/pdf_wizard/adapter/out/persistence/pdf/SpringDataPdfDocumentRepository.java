package com.devlab.pdf_wizard.adapter.out.persistence.pdf;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataPdfDocumentRepository
        extends JpaRepository<PdfDocumentEntity, UUID> {
}
