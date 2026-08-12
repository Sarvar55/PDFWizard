package com.devlab.pdf_wizard.adapter.in.web.model;

import java.util.Map;

import com.devlab.pdf_wizard.application.in.command.CreatePdfCommand;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public record CreatePdfRequest(
        @NotBlank(message = "File name cannot be blank")
        String fileName,

        @NotBlank(message = "Title cannot be blank")
        String title,

        @NotEmpty(message = "Content cannot be empty")
        Map<String, Object> data) {

    private static final String DEFAULT_CREATOR = "system";

    public CreatePdfCommand toCommand() {
        return CreatePdfCommand.of(fileName, title, data, DEFAULT_CREATOR);
    }
}
