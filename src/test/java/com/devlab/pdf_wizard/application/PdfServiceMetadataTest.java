package com.devlab.pdf_wizard.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.devlab.pdf_wizard.application.in.query.GetPdfMetadataQuery;
import com.devlab.pdf_wizard.application.out.PdfLoadPort;
import com.devlab.pdf_wizard.domain.exception.PdfDocumentNotFoundException;
import com.devlab.pdf_wizard.domain.model.ContentType;
import com.devlab.pdf_wizard.domain.model.PdfDocument;

class PdfServiceMetadataTest {

    @Test
    @DisplayName("should return PDF metadata when document exists")
    void shouldReturnPdfMetadataWhenDocumentExists() {
        PdfDocument document = PdfDocument.create(
                "invoice.pdf",
                "documents/invoice.pdf",
                ContentType.PDF,
                128L,
                "sarvar");
        PdfLoadPort pdfLoadPort = id -> Optional.of(document);
        PdfService service = createService(pdfLoadPort);

        PdfDocument result = service.execute(GetPdfMetadataQuery.of(document.getId()));

        assertThat(result).isSameAs(document);
    }

    @Test
    @DisplayName("should throw exception when PDF document does not exist")
    void shouldThrowExceptionWhenPdfDocumentDoesNotExist() {
        UUID id = UUID.randomUUID();
        PdfLoadPort pdfLoadPort = ignoredId -> Optional.empty();
        PdfService service = createService(pdfLoadPort);

        assertThatThrownBy(() -> service.execute(GetPdfMetadataQuery.of(id)))
                .isInstanceOf(PdfDocumentNotFoundException.class)
                .satisfies(exception -> assertThat(((PdfDocumentNotFoundException) exception).getDetails())
                        .containsEntry("id", id));
    }

    private PdfService createService(PdfLoadPort pdfLoadPort) {
        return new PdfService(null, null, null, pdfLoadPort, null, null, null, null);
    }
}
