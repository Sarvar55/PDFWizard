package com.devlab.pdf_wizard.domain.model;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import com.devlab.pdf_wizard.domain.exception.CommonErrorType;
import com.devlab.pdf_wizard.domain.exception.ValidationException;

public class PdfDocument {

    private final UUID id;
    private final String fileName;
    private final String storedFileName;
    private final ContentType contentType;
    private final Long size;
    private final String createdBy;
    private final Instant createdAt;
    private Instant updatedAt;

    private PdfDocument(UUID id, String fileName, String storedFileName,
            ContentType contentType, Long size, String createdBy,
            Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.fileName = fileName;
        this.storedFileName = storedFileName;
        this.contentType = contentType;
        this.size = size;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static PdfDocument create(String fileName, String storedFileName,
            ContentType contentType, Long size, String createdBy) {
        Instant now = Instant.now();

        return new PdfDocument(
                UUID.randomUUID(),
                validateFileName(fileName),
                validateStoredFileName(storedFileName),
                validateContentType(contentType),
                validateSize(size),
                validateCreatedBy(createdBy),
                now,
                now);
    }

    public static PdfDocument restore(
            UUID id,
            String fileName,
            String storedFileName,
            ContentType contentType,
            Long size,
            String createdBy,
            Instant createdAt,
            Instant updatedAt) {
        return new PdfDocument(
                validateId(id),
                validateFileName(fileName),
                validateStoredFileName(storedFileName),
                validateContentType(contentType),
                validateSize(size),
                validateCreatedBy(createdBy),
                validateTimestamp("createdAt", createdAt),
                validateTimestamp("updatedAt", updatedAt));
    }

    private static UUID validateId(UUID id) {
        if (id == null) {
            throw validationError("id", "Document id cannot be null");
        }
        return id;
    }

    private static String validateFileName(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            throw validationError("fileName", "File name cannot be null or empty");
        }
        if (!fileName.toLowerCase().endsWith(".pdf")) {
            throw validationError("fileName", "File name must have a .pdf extension");
        }
        return fileName.trim();
    }

    private static String validateStoredFileName(String storedFileName) {
        if (storedFileName == null || storedFileName.isBlank()) {
            throw validationError("storedFileName", "Stored file name cannot be null or empty");
        }
        return storedFileName.trim();
    }

    private static ContentType validateContentType(ContentType contentType) {
        if (contentType == null || !contentType.isPdf()) {
            throw validationError("contentType", "Unsupported content type: " + contentType);
        }
        return contentType;
    }

    private static Long validateSize(Long size) {
        if (size == null || size <= 0) {
            throw validationError("size", "PDF size must be greater than zero");
        }
        return size;
    }

    private static String validateCreatedBy(String createdBy) {
        if (createdBy == null || createdBy.isBlank()) {
            throw validationError("createdBy", "Creator cannot be null or empty");
        }
        return createdBy.trim();
    }

    private static Instant validateTimestamp(String fieldName, Instant timestamp) {
        if (timestamp == null) {
            throw validationError(fieldName, fieldName + " cannot be null");
        }
        return timestamp;
    }

    private static ValidationException validationError(String fieldName, String message) {
        return ValidationException.of(
                CommonErrorType.VALIDATION_FAILED,
                Map.of(fieldName, message));
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void touch() {
        this.updatedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public String getFileName() {
        return fileName;
    }

    public String getStoredFileName() {
        return storedFileName;
    }

    public ContentType getContentType() {
        return contentType;
    }

    public Long getSize() {
        return size;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
