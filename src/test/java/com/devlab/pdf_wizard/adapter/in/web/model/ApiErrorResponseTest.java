package com.devlab.pdf_wizard.adapter.in.web.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.devlab.pdf_wizard.domain.exception.PdfDocumentNotFoundException;

class ApiErrorResponseTest {

    @Test
    @DisplayName("should map domain exception to API error response")
    void shouldMapDomainExceptionToApiErrorResponse() {
        UUID id = UUID.randomUUID();
        PdfDocumentNotFoundException exception = PdfDocumentNotFoundException.forId(id);

        ApiErrorResponse response = ApiErrorResponse.from(exception);

        assertThat(response.code()).isEqualTo("RESOURCE_NOT_FOUND");
        assertThat(response.message()).isEqualTo("Resource not found");
        assertThat(response.validationErrors()).isNull();
        assertThat(response.details()).containsEntry("id", id);
        assertThat(response.timestamp()).isNotNull();
    }
}
