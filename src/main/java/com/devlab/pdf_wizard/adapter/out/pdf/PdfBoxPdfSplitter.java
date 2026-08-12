package com.devlab.pdf_wizard.adapter.out.pdf;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.stereotype.Component;

import com.devlab.pdf_wizard.application.model.PdfGenerationResponse;
import com.devlab.pdf_wizard.application.model.PdfSplitRequest;
import com.devlab.pdf_wizard.application.out.pdf.PdfSplitter;
import com.devlab.pdf_wizard.domain.exception.PdfGenerationException;

@Component
public class PdfBoxPdfSplitter implements PdfSplitter {

    private static final String PDF_CONTENT_TYPE = "application/pdf";

    @Override
    public List<PdfGenerationResponse> split(PdfSplitRequest request) {
        Path temporaryFile = null;

        try {
            temporaryFile = copyToTemporaryFile(request);

            try (PDDocument source = Loader.loadPDF(
                    temporaryFile.toFile(),
                    IOUtils.createTempFileOnlyStreamCache())) {
                Splitter splitter = new Splitter();
                splitter.setSplitAtPage(1);
                splitter.setStreamCacheCreateFunction(IOUtils.createTempFileOnlyStreamCache());

                List<PDDocument> pages = splitter.split(source);
                return savePages(pages, request.outputFileNamePrefix());
            }
        } catch (IOException | IllegalArgumentException exception) {
            throw new PdfGenerationException("PDF document could not be split", exception);
        } finally {
            deleteTemporaryFile(temporaryFile);
        }
    }

    private Path copyToTemporaryFile(PdfSplitRequest request) throws IOException {
        Path temporaryFile = Files.createTempFile("pdf-wizard-split-", ".pdf");

        try (InputStream input = request.file().contentSource().openStream();
                OutputStream output = Files.newOutputStream(temporaryFile)) {
            input.transferTo(output);
            return temporaryFile;
        } catch (IOException | RuntimeException exception) {
            Files.deleteIfExists(temporaryFile);
            throw exception;
        }
    }

    private List<PdfGenerationResponse> savePages(
            List<PDDocument> pages,
            String outputFileNamePrefix) throws IOException {
        List<PdfGenerationResponse> responses = new ArrayList<>(pages.size());

        try {
            for (int index = 0; index < pages.size(); index++) {
                PDDocument page = pages.get(index);

                try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
                    page.save(output);
                    responses.add(new PdfGenerationResponse(
                            outputFileNamePrefix + "-page-" + (index + 1) + ".pdf",
                            PDF_CONTENT_TYPE,
                            output.toByteArray()));
                }
            }
            return List.copyOf(responses);
        } finally {
            closeDocuments(pages);
        }
    }

    private void closeDocuments(List<PDDocument> documents) {
        for (PDDocument document : documents) {
            IOUtils.closeQuietly(document);
        }
    }

    private void deleteTemporaryFile(Path temporaryFile) {
        if (temporaryFile == null) {
            return;
        }

        try {
            Files.deleteIfExists(temporaryFile);
        } catch (IOException ignored) {
            temporaryFile.toFile().deleteOnExit();
        }
    }
}
