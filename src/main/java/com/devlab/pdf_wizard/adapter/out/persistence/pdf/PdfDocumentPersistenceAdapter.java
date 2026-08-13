package com.devlab.pdf_wizard.adapter.out.persistence.pdf;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.devlab.pdf_wizard.application.out.PdfDeletePort;
import com.devlab.pdf_wizard.application.out.PdfLoadPort;
import com.devlab.pdf_wizard.application.out.PdfSavePort;
import com.devlab.pdf_wizard.domain.model.PdfDocument;

@Component
public class PdfDocumentPersistenceAdapter
        implements PdfSavePort, PdfLoadPort, PdfDeletePort {

    private final SpringDataPdfDocumentRepository repository;

    public PdfDocumentPersistenceAdapter(SpringDataPdfDocumentRepository repository) {
        this.repository = repository;
    }

    @Override
    public PdfDocument save(PdfDocument document) {
        return PdfDocumentMapper.toDomain(
                repository.save(PdfDocumentMapper.toEntity(document)));
    }

    @Override
    public Optional<PdfDocument> findById(UUID id) {
        return repository.findById(id).map(PdfDocumentMapper::toDomain);
    }

    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }
}
