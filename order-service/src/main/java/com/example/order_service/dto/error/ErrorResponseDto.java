package com.example.order_service.dto.error;

public record ErrorResponseDto(int status, String error, String message) {
}
