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
import com.devlab.pdf_wizard.application.model.PdfSplitRequest;
import com.devlab.pdf_wizard.application.model.UploadedPdf;
import com.devlab.pdf_wizard.domain.exception.PdfGenerationException;

class PdfBoxPdfSplitterTest {

    private final PdfBoxPdfSplitter splitter = new PdfBoxPdfSplitter();

    @Test
    @DisplayName("should create one PDF for every source page")
    void shouldCreateOnePdfForEverySourcePage() throws IOException {
        byte[] sourceContent = createPdf(3);
        UploadedPdf source = uploadedPdf("contract.pdf", sourceContent);

        List<PdfGenerationResponse> responses = splitter.split(
                new PdfSplitRequest("contract", source));

        assertThat(responses).hasSize(3);
        assertThat(responses)
                .extracting(PdfGenerationResponse::fileName)
                .containsExactly(
                        "contract-page-1.pdf",
                        "contract-page-2.pdf",
                        "contract-page-3.pdf");

        for (PdfGenerationResponse response : responses) {
            assertThat(response.contentType()).isEqualTo("application/pdf");
            try (PDDocument page = Loader.loadPDF(response.content())) {
                assertThat(page.getNumberOfPages()).isEqualTo(1);
            }
        }
    }

    @Test
    @DisplayName("should reject invalid source PDF")
    void shouldRejectInvalidSourcePdf() {
        UploadedPdf source = uploadedPdf("invalid.pdf", new byte[] { 1, 2, 3 });

        assertThatThrownBy(() -> splitter.split(new PdfSplitRequest("invalid", source)))
                .isInstanceOf(PdfGenerationException.class)
                .hasMessage("PDF document could not be split");
    }

    private UploadedPdf uploadedPdf(String fileName, byte[] content) {
        return UploadedPdf.of(
                fileName,
                "application/pdf",
                content.length,
                () -> new ByteArrayInputStream(content));
    }

    private byte[] createPdf(int pageCount) throws IOException {
        try (PDDocument document = new PDDocument();
                ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            for (int index = 0; index < pageCount; index++) {
                document.addPage(new PDPage());
            }
            document.save(output);
            return output.toByteArray();
        }
    }
}
