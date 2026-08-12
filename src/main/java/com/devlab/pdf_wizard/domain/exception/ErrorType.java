package com.devlab.pdf_wizard.domain.exception;

import org.springframework.http.HttpStatus;

public interface ErrorType {
    String getCode();

    String getMessage();

    HttpStatus getHttpStatus();
}
