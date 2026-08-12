package com.devlab.pdf_wizard.domain.model;

import java.util.Set;

public enum ContentType {

    PDF("pdf", "application/pdf", Set.of("application/pdf", "application/x-pdf"));

    private final String extension;
    private final String primaryMimeType;
    private final Set<String> mimeTypes;

    ContentType(String extension, String primaryMimeType, Set<String> mimeTypes) {
        this.extension = extension;
        this.primaryMimeType = primaryMimeType;
        this.mimeTypes = mimeTypes;
    }

    public String getExtension() {
        return extension;
    }

    public Set<String> getMimeTypes() {
        return mimeTypes;
    }

    public String getPrimaryMimeType() {
        return primaryMimeType;
    }

    public boolean isPdf() {
        return this.equals(ContentType.PDF);
    }

    public static ContentType fromExtension(String extension) {
        for (ContentType contentType : values()) {
            if (contentType.extension.equals(extension)) {
                return contentType;
            }
        }
        throw new IllegalArgumentException("Unsupported extension: " + extension);
    }

    public static ContentType fromMimeType(String mimeType) {
        for (ContentType contentType : values()) {
            if (contentType.mimeTypes.contains(mimeType)) {
                return contentType;
            }
        }
        throw new IllegalArgumentException("Unsupported mime type: " + mimeType);
    }

}
