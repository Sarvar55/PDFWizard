package com.devlab.pdf_wizard.adapter.in.web.model;

import java.util.UUID;

import com.devlab.pdf_wizard.application.in.command.SendPdfEmailCommand;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SendPdfEmailRequest(
        @NotNull(message = "Document id cannot be null")
        UUID documentId,

        @NotBlank(message = "Recipient email cannot be blank")
        @Email(message = "Recipient email is invalid")
        String recipient) {

    public SendPdfEmailCommand toCommand() {
        return SendPdfEmailCommand.of(documentId, recipient);
    }
}
