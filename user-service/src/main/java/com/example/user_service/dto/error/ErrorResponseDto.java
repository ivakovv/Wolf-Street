package com.example.user_service.dto.error;

public record ErrorResponseDto(int status, String error, String message) {
}
