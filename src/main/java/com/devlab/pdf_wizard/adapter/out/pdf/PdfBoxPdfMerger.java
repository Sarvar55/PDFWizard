package com.devlab.pdf_wizard.adapter.out.pdf;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.devlab.pdf_wizard.application.model.PdfGenerationResponse;
import com.devlab.pdf_wizard.application.model.PdfMergeRequest;
import com.devlab.pdf_wizard.application.model.UploadedPdf;
import com.devlab.pdf_wizard.application.out.pdf.PdfMerger;
import com.devlab.pdf_wizard.domain.exception.PdfGenerationException;

@Component
public class PdfBoxPdfMerger implements PdfMerger {

    private static final Logger LOGGER = LoggerFactory.getLogger(PdfBoxPdfMerger.class);
    private static final String PDF_CONTENT_TYPE = "application/pdf";

    @Override
    public PdfGenerationResponse merge(PdfMergeRequest request) {
        List<Path> temporaryFiles = new ArrayList<>();

        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            PDFMergerUtility merger = new PDFMergerUtility();

            for (UploadedPdf file : request.files()) {
                Path temporaryFile = copyToTemporaryFile(file);
                temporaryFiles.add(temporaryFile);
                merger.addSource(temporaryFile.toFile());
            }

            merger.setDestinationStream(output);
            merger.mergeDocuments(IOUtils.createTempFileOnlyStreamCache());

            return new PdfGenerationResponse(
                    request.outputFileName(),
                    PDF_CONTENT_TYPE,
                    output.toByteArray());
        } catch (IOException | IllegalArgumentException exception) {
            throw new PdfGenerationException("PDF documents could not be merged", exception);
        } finally {
            deleteTemporaryFiles(temporaryFiles);
        }
    }

    private Path copyToTemporaryFile(UploadedPdf file) throws IOException {
        Path temporaryFile = Files.createTempFile("pdf-wizard-merge-", ".pdf");

        try (InputStream input = file.contentSource().openStream();
                OutputStream output = Files.newOutputStream(temporaryFile)) {
            input.transferTo(output);
            return temporaryFile;
        } catch (IOException | RuntimeException exception) {
            Files.deleteIfExists(temporaryFile);
            throw exception;
        }
    }

    private void deleteTemporaryFiles(List<Path> temporaryFiles) {
        for (Path temporaryFile : temporaryFiles) {
            try {
                Files.deleteIfExists(temporaryFile);
            } catch (IOException exception) {
                LOGGER.warn("Temporary merge file could not be deleted: {}", temporaryFile, exception);
            }
        }
    }
}
