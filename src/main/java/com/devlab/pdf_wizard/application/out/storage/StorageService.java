package com.devlab.pdf_wizard.application.out.storage;

import java.io.InputStream;

public interface StorageService {
    String store(
            String fileName,
            String contentType,
            byte[] content);

    InputStream load(String storedFileName);

    void delete(String storedFileName);
}
