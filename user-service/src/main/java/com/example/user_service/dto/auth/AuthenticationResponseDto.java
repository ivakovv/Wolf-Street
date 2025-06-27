package com.example.user_service.dto.auth;

public record AuthenticationResponseDto(String accessToken, String refreshToken) {
}
