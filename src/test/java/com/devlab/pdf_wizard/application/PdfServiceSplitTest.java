package com.devlab.pdf_wizard.application;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.devlab.pdf_wizard.application.in.command.SplitPdfCommand;
import com.devlab.pdf_wizard.application.model.PdfGenerationResponse;
import com.devlab.pdf_wizard.application.model.UploadedPdf;
import com.devlab.pdf_wizard.application.out.storage.StorageService;
import com.devlab.pdf_wizard.domain.model.PdfDocument;

class PdfServiceSplitTest {

    @Test
    @DisplayName("should store metadata for every split page")
    void shouldStoreMetadataForEverySplitPage() {
        List<String> storedFileNames = new ArrayList<>();
        StorageService storageService = storageService(storedFileNames);
        PdfService service = new PdfService(
                null,
                storageService,
                document -> document,
                null,
                null,
                null,
                request -> List.of(
                        response("contract-page-1.pdf"),
                        response("contract-page-2.pdf")),
                null);
        UploadedPdf source = UploadedPdf.of(
                "contract.pdf",
                "application/pdf",
                1,
                () -> new ByteArrayInputStream(new byte[] { 1 }));

        List<PdfDocument> documents = service.execute(
                SplitPdfCommand.of("contract", source, "system"));

        assertThat(documents)
                .extracting(PdfDocument::getFileName)
                .containsExactly("contract-page-1.pdf", "contract-page-2.pdf");
        assertThat(storedFileNames)
                .containsExactly("contract-page-1.pdf", "contract-page-2.pdf");
    }

    private PdfGenerationResponse response(String fileName) {
        return new PdfGenerationResponse(fileName, "application/pdf", new byte[] { 1 });
    }

    private StorageService storageService(List<String> storedFileNames) {
        return new StorageService() {
            @Override
            public String store(String fileName, String contentType, byte[] content) {
                storedFileNames.add(fileName);
                return "documents/" + fileName;
            }

            @Override
            public InputStream load(String storedFileName) {
                throw new UnsupportedOperationException("Not used by this test");
            }

            @Override
            public void delete(String storedFileName) {
                throw new UnsupportedOperationException("Not used by this test");
            }
        };
    }
}
