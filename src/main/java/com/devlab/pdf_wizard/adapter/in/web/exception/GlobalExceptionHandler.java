package com.devlab.pdf_wizard.adapter.in.web.exception;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.devlab.pdf_wizard.adapter.in.web.model.ApiErrorResponse;
import com.devlab.pdf_wizard.domain.exception.BaseException;
import com.devlab.pdf_wizard.domain.exception.CommonErrorType;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ApiErrorResponse> handleBaseException(BaseException exception) {
        ApiErrorResponse response = ApiErrorResponse.from(exception);

        return ResponseEntity
                .status(exception.getStatus())
                .body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationException(
            MethodArgumentNotValidException exception) {
        Map<String, String> validationErrors = new LinkedHashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(error ->
                validationErrors.putIfAbsent(error.getField(), error.getDefaultMessage()));

        CommonErrorType errorType = CommonErrorType.VALIDATION_FAILED;
        ApiErrorResponse response = new ApiErrorResponse(
                errorType.getCode(),
                errorType.getMessage(),
                Map.copyOf(validationErrors),
                null,
                Instant.now());

        return ResponseEntity
                .status(errorType.getHttpStatus())
                .body(response);
    }
}
