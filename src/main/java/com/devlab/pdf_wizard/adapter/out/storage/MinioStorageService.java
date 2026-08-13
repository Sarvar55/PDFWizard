package com.devlab.pdf_wizard.adapter.out.storage;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.devlab.pdf_wizard.application.out.storage.StorageService;
import com.devlab.pdf_wizard.domain.exception.StorageOperationException;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;

@Component
public class MinioStorageService implements StorageService {

    private final MinioClient minioClient;
    private final String bucket;

    public MinioStorageService(
            MinioClient minioClient,
            @Value("${pdf-wizard.storage.minio.bucket:pdf-documents}") String bucket) {
        this.minioClient = minioClient;
        this.bucket = bucket;
    }

    @Override
    public String store(String fileName, String contentType, byte[] content) {
        String objectName = UUID.randomUUID() + "-" + sanitize(fileName);

        try (ByteArrayInputStream input = new ByteArrayInputStream(content)) {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectName)
                    .contentType(contentType)
                    .stream(input, content.length, -1)
                    .build());
            return objectName;
        } catch (Exception exception) {
            throw new StorageOperationException("PDF could not be stored", exception);
        }
    }

    @Override
    public InputStream load(String storedFileName) {
        try {
            return minioClient.getObject(GetObjectArgs.builder()
                    .bucket(bucket)
                    .object(storedFileName)
                    .build());
        } catch (Exception exception) {
            throw new StorageOperationException("PDF could not be loaded", exception);
        }
    }

    @Override
    public void delete(String storedFileName) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucket)
                    .object(storedFileName)
                    .build());
        } catch (Exception exception) {
            throw new StorageOperationException("PDF could not be deleted", exception);
        }
    }

    private String sanitize(String fileName) {
        String baseName = fileName == null ? "document.pdf" : fileName.replace('\\', '/');
        int lastSeparator = baseName.lastIndexOf('/');
        if (lastSeparator >= 0) {
            baseName = baseName.substring(lastSeparator + 1);
        }
        String sanitized = baseName.replaceAll("[^a-zA-Z0-9._-]", "_");
        return sanitized.isBlank() ? "document.pdf" : sanitized;
    }
}
