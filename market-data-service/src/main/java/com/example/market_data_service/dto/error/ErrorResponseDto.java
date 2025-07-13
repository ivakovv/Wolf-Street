package com.example.market_data_service.dto.error;

public record ErrorResponseDto(int status, String error, String message) {
}
