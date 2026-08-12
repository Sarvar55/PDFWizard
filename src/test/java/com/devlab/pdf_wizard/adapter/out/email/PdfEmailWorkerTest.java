package com.devlab.pdf_wizard.adapter.out.email;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.devlab.pdf_wizard.application.in.command.SendPdfEmailCommand;
import com.devlab.pdf_wizard.application.model.PdfEmailMessage;
import com.devlab.pdf_wizard.application.out.storage.StorageService;
import com.devlab.pdf_wizard.domain.model.ContentType;
import com.devlab.pdf_wizard.domain.model.PdfDocument;

class PdfEmailWorkerTest {

    @Test
    @DisplayName("should load attachment and send email on worker thread")
    void shouldLoadAttachmentAndSendEmailOnWorkerThread() {
        PdfDocument document = PdfDocument.create(
                "invoice.pdf",
                "documents/invoice.pdf",
                ContentType.PDF,
                3L,
                "system");
        AtomicBoolean streamClosed = new AtomicBoolean();
        AtomicReference<PdfEmailMessage> sentMessage = new AtomicReference<>();
        StorageService storageService = new StorageService() {
            @Override
            public String store(String fileName, String contentType, byte[] content) {
                throw new UnsupportedOperationException();
            }

            @Override
            public ByteArrayInputStream load(String storedFileName) {
                return new ByteArrayInputStream(new byte[] { 1, 2, 3 }) {
                    @Override
                    public void close() {
                        streamClosed.set(true);
                    }
                };
            }

            @Override
            public void delete(String storedFileName) {
                throw new UnsupportedOperationException();
            }
        };
        PdfEmailWorker worker = new PdfEmailWorker(
                id -> Optional.of(document),
                storageService,
                sentMessage::set);

        worker.send(SendPdfEmailCommand.of(document.getId(), "user@example.com"));

        assertThat(sentMessage.get().recipient()).isEqualTo("user@example.com");
        assertThat(sentMessage.get().attachmentFileName()).isEqualTo("invoice.pdf");
        assertThat(sentMessage.get().attachmentContent()).containsExactly(1, 2, 3);
        assertThat(streamClosed).isTrue();
    }
}
