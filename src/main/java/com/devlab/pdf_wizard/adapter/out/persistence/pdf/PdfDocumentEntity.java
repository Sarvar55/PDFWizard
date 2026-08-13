package com.devlab.pdf_wizard.adapter.out.persistence.pdf;

import java.time.Instant;
import java.util.UUID;

import com.devlab.pdf_wizard.domain.model.ContentType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "pdf_documents")
public class PdfDocumentEntity {

    @Id
    private UUID id;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "stored_file_name", nullable = false, unique = true)
    private String storedFileName;

    @Enumerated(EnumType.STRING)
    @Column(name = "content_type", nullable = false, length = 40)
    private ContentType contentType;

    @Column(nullable = false)
    private Long size;

    @Column(name = "created_by", nullable = false, length = 320)
    private String createdBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected PdfDocumentEntity() {
    }

    public PdfDocumentEntity(UUID id, String fileName, String storedFileName,
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

    public UUID getId() { return id; }
    public String getFileName() { return fileName; }
    public String getStoredFileName() { return storedFileName; }
    public ContentType getContentType() { return contentType; }
    public Long getSize() { return size; }
    public String getCreatedBy() { return createdBy; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
