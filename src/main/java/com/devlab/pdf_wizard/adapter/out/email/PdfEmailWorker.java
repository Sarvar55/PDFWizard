package com.devlab.pdf_wizard.adapter.out.email;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.stereotype.Component;

import com.devlab.pdf_wizard.application.in.command.SendPdfEmailCommand;
import com.devlab.pdf_wizard.application.model.PdfEmailMessage;
import com.devlab.pdf_wizard.application.out.PdfLoadPort;
import com.devlab.pdf_wizard.application.out.email.EmailSender;
import com.devlab.pdf_wizard.application.out.storage.StorageService;
import com.devlab.pdf_wizard.domain.exception.EmailSendingException;
import com.devlab.pdf_wizard.domain.exception.PdfDocumentNotFoundException;
import com.devlab.pdf_wizard.domain.model.PdfDocument;

@Component
public class PdfEmailWorker {

    private final PdfLoadPort pdfLoadPort;
    private final StorageService storageService;
    private final EmailSender emailSender;

    public PdfEmailWorker(PdfLoadPort pdfLoadPort,
            StorageService storageService,
            EmailSender emailSender) {
        this.pdfLoadPort = pdfLoadPort;
        this.storageService = storageService;
        this.emailSender = emailSender;
    }

    public void send(SendPdfEmailCommand command) {
        PdfDocument document = pdfLoadPort.findById(command.documentId())
                .orElseThrow(() -> PdfDocumentNotFoundException.forId(command.documentId()));

        try (InputStream content = storageService.load(document.getStoredFileName())) {
            PdfEmailMessage message = new PdfEmailMessage(
                    command.recipient(),
                    "Your PDF document: " + document.getFileName(),
                    "Your requested PDF document is attached.",
                    document.getFileName(),
                    document.getContentType().getPrimaryMimeType(),
                    content.readAllBytes());

            emailSender.send(message);
        } catch (IOException exception) {
            throw new EmailSendingException("PDF attachment could not be read", exception);
        }
    }
}
