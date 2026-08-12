package com.devlab.pdf_wizard.application.model;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public record PdfDownloadResult(
        String fileName,
        String contentType,
        long contentLength,
        InputStream content) implements AutoCloseable {

    public PdfDownloadResult {
        if (fileName == null || fileName.isBlank()) {
            throw new IllegalArgumentException("File name cannot be blank");
        }
        if (contentType == null || contentType.isBlank()) {
            throw new IllegalArgumentException("Content type cannot be blank");
        }

        if (contentLength <= 0) {
            throw new IllegalArgumentException("Content length must be greater than zero");
        }
        Objects.requireNonNull(content, "PDF content stream cannot be null");
    }

    @Override
    public void close() throws IOException {
        content.close();
    }
}
