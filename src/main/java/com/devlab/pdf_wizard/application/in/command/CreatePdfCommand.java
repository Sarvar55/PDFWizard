package com.devlab.pdf_wizard.application.in.command;

import com.devlab.pdf_wizard.domain.exception.CommonErrorType;
import com.devlab.pdf_wizard.domain.exception.ValidationException;

import java.util.Map;

public record CreatePdfCommand(
        String fileName,
        String title,
        Map<String, Object> data,
        String createdBy) {

    public CreatePdfCommand(String fileName, String title, Map<String, Object> data, String createdBy) {
        if (fileName == null || fileName.isBlank()) {
            throw ValidationException.of(
                    CommonErrorType.VALIDATION_FAILED,
                    Map.of("fileName", "File name cannot be blank"));
        }
        if (title == null || title.isBlank()) {
            throw ValidationException.of(
                    CommonErrorType.VALIDATION_FAILED,
                    Map.of("title", "Title cannot be blank"));
        }
        if (data == null || data.isEmpty()) {
            throw ValidationException.of(
                    CommonErrorType.VALIDATION_FAILED,
                    Map.of("data", "Content cannot be blank"));
        }
        if (createdBy == null || createdBy.isBlank()) {
            throw ValidationException.of(
                    CommonErrorType.VALIDATION_FAILED,
                    Map.of("createdBy", "Creator cannot be blank"));
        }
        this.fileName = fileName;
        this.title = title;
        this.data = data;
        this.createdBy = createdBy;
    }

    public static CreatePdfCommand of(String fileName, String title, Map<String, Object> data, String createdBy) {
        return new CreatePdfCommand(fileName, title, data, createdBy);
    }
}
