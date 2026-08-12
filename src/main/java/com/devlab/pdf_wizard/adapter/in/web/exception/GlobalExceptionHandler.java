package com.devlab.pdf_wizard.adapter.in.web.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.devlab.pdf_wizard.adapter.in.web.model.ApiErrorResponse;
import com.devlab.pdf_wizard.domain.exception.BaseException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ApiErrorResponse> handleBaseException(BaseException exception) {
        ApiErrorResponse response = ApiErrorResponse.from(exception);

        return ResponseEntity
                .status(exception.getStatus())
                .body(response);
    }
}
