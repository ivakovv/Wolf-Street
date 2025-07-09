package com.example.portfolio_service.dto.error;

public record ErrorResponseDto(int status, String error, String message) {
}
