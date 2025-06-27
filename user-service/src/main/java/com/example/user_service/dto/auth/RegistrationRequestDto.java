package com.example.user_service.dto.auth;

public record RegistrationRequestDto(
        String username,
        String password,
        String email,
        String firstname,
        String lastname,
        String phone
) {
}
