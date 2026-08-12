package com.devlab.pdf_wizard.adapter.out.pdf;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.devlab.pdf_wizard.application.model.PdfGenerationResponse;
import com.devlab.pdf_wizard.application.model.PdfMergeRequest;
import com.devlab.pdf_wizard.application.model.UploadedPdf;
import com.devlab.pdf_wizard.domain.exception.PdfGenerationException;

class PdfBoxPdfMergerTest {

    private final PdfBoxPdfMerger merger = new PdfBoxPdfMerger();

    @Test
    @DisplayName("should merge pages from all PDF documents")
    void shouldMergePagesFromAllPdfDocuments() throws IOException {
        UploadedPdf first = uploadedPdf("first.pdf", createPdf(1));
        UploadedPdf second = uploadedPdf("second.pdf", createPdf(2));
        PdfMergeRequest request = new PdfMergeRequest("merged.pdf", List.of(first, second));

        PdfGenerationResponse response = merger.merge(request);

        assertThat(response.fileName()).isEqualTo("merged.pdf");
        assertThat(response.contentType()).isEqualTo("application/pdf");
        assertThat(response.content()).startsWith((byte) '%', (byte) 'P', (byte) 'D', (byte) 'F');

        try (PDDocument mergedDocument = Loader.loadPDF(response.content())) {
            assertThat(mergedDocument.getNumberOfPages()).isEqualTo(3);
        }
    }

    @Test
    @DisplayName("should throw exception when a source is not a valid PDF")
    void shouldThrowExceptionWhenSourceIsNotAValidPdf() {
        UploadedPdf invalid = uploadedPdf("invalid.pdf", new byte[] { 1, 2, 3 });
        UploadedPdf valid = uploadedPdf("valid.pdf", createPdfUnchecked(1));
        PdfMergeRequest request = new PdfMergeRequest("merged.pdf", List.of(invalid, valid));

        assertThatThrownBy(() -> merger.merge(request))
                .isInstanceOf(PdfGenerationException.class)
                .hasMessage("PDF documents could not be merged");
    }

    private UploadedPdf uploadedPdf(String fileName, byte[] content) {
        return UploadedPdf.of(
                fileName,
                "application/pdf",
                content.length,
                () -> new ByteArrayInputStream(content));
    }

    private byte[] createPdfUnchecked(int pageCount) {
        try {
            return createPdf(pageCount);
        } catch (IOException exception) {
            throw new IllegalStateException(exception);
        }
    }

    private byte[] createPdf(int pageCount) throws IOException {
        try (PDDocument document = new PDDocument();
                ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            for (int page = 0; page < pageCount; page++) {
                document.addPage(new PDPage());
            }
            document.save(output);
            return output.toByteArray();
        }
    }
}
