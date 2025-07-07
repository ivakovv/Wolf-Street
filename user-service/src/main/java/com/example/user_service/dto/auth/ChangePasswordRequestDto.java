package com.example.user_service.dto.auth;

public record ChangePasswordRequestDto(String newPassword, String currentPassword) {
}
