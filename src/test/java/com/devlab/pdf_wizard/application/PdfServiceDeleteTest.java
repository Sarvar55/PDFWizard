package com.devlab.pdf_wizard.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.devlab.pdf_wizard.application.in.command.DeletePdfCommand;
import com.devlab.pdf_wizard.application.out.PdfDeletePort;
import com.devlab.pdf_wizard.application.out.PdfLoadPort;
import com.devlab.pdf_wizard.application.out.storage.StorageService;
import com.devlab.pdf_wizard.domain.exception.PdfDocumentNotFoundException;
import com.devlab.pdf_wizard.domain.model.ContentType;
import com.devlab.pdf_wizard.domain.model.PdfDocument;

class PdfServiceDeleteTest {

    @Test
    @DisplayName("should delete storage object before metadata")
    void shouldDeleteStorageObjectBeforeMetadata() {
        PdfDocument document = PdfDocument.create(
                "invoice.pdf",
                "documents/invoice.pdf",
                ContentType.PDF,
                256L,
                "system");
        List<String> operations = new ArrayList<>();
        PdfLoadPort pdfLoadPort = id -> Optional.of(document);
        StorageService storageService = storageServiceRecordingDeletes(operations);
        PdfDeletePort pdfDeletePort = id -> operations.add("metadata:" + id);
        PdfService service = new PdfService(
                null,
                storageService,
                null,
                pdfLoadPort,
                pdfDeletePort,
                null,
                null,
                null);

        service.execute(DeletePdfCommand.of(document.getId()));

        assertThat(operations).containsExactly(
                "storage:" + document.getStoredFileName(),
                "metadata:" + document.getId());
    }

    @Test
    @DisplayName("should throw exception when document to delete does not exist")
    void shouldThrowExceptionWhenDocumentToDeleteDoesNotExist() {
        UUID id = UUID.randomUUID();
        PdfLoadPort pdfLoadPort = ignoredId -> Optional.empty();
        PdfService service = new PdfService(
                null,
                storageServiceRecordingDeletes(new ArrayList<>()),
                null,
                pdfLoadPort,
                ignoredId -> {
                },
                null,
                null,
                null);

        assertThatThrownBy(() -> service.execute(DeletePdfCommand.of(id)))
                .isInstanceOf(PdfDocumentNotFoundException.class);
    }

    private StorageService storageServiceRecordingDeletes(List<String> operations) {
        return new StorageService() {
            @Override
            public String store(String fileName, String contentType, byte[] content) {
                throw new UnsupportedOperationException("Not used by this test");
            }

            @Override
            public InputStream load(String storedFileName) {
                throw new UnsupportedOperationException("Not used by this test");
            }

            @Override
            public void delete(String storedFileName) {
                operations.add("storage:" + storedFileName);
            }
        };
    }
}
