package com.example.analytic_service.controller.error;

import com.example.analytic_service.dto.error.ErrorResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionController {
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDto> handleClientExists(IllegalArgumentException ex) {
        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Illegal arguments",
                ex.getMessage()
        );
    }

    private ResponseEntity<ErrorResponseDto> buildErrorResponse(
            HttpStatus status,
            String error,
            String message) {

        ErrorResponseDto errorResponse = new ErrorResponseDto(
                status.value(),
                error,
                message
        );

        return new ResponseEntity<>(errorResponse, status);
    }
}
