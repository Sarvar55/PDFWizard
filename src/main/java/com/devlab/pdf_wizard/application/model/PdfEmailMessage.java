package com.devlab.pdf_wizard.application.model;

import java.util.Objects;

public record PdfEmailMessage(
        String recipient,
        String subject,
        String body,
        String attachmentFileName,
        String attachmentContentType,
        byte[] attachmentContent) {

    public PdfEmailMessage {
        Objects.requireNonNull(attachmentContent, "Attachment content cannot be null");
        attachmentContent = attachmentContent.clone();
    }

    @Override
    public byte[] attachmentContent() {
        return attachmentContent.clone();
    }
}
