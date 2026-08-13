package com.devlab.pdf_wizard.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.minio.MinioClient;

@Configuration
public class StorageConfig {

    @Bean
    public MinioClient minioClient(
            @Value("${pdf-wizard.storage.minio.endpoint:http://localhost:9000}") String endpoint,
            @Value("${pdf-wizard.storage.minio.access-key:pdfwizard}") String accessKey,
            @Value("${pdf-wizard.storage.minio.secret-key}") String secretKey) {
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }
}
