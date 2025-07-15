package com.example.analytic_service.dto.error;

public record ErrorResponseDto(int status, String error, String message) {
}
