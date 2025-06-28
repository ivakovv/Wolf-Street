package com.example.user_service.dto.user;

import java.time.LocalDateTime;

public record UserResponseDto(String username,
                              String email,
                              String firstname,
                              String lastname,
                              String phone,
                              LocalDateTime created_at,
                              LocalDateTime updated_at) {
}
