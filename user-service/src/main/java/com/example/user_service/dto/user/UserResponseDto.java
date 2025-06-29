package com.example.user_service.dto.user;

import java.time.OffsetDateTime;

public record UserResponseDto(String username,
                              String email,
                              String firstname,
                              String lastname,
                              String phone,
                              OffsetDateTime created_at,
                              OffsetDateTime updated_at) {
}
