package com.example.user_service.dto.event;

import java.time.OffsetDateTime;

public record UserRegistredEvent(Long id,
                                 String username,
                                 String email,
                                 String firstname,
                                 String lastname,
                                 String phone,
                                 OffsetDateTime created_at,
                                 OffsetDateTime updated_at) {}
