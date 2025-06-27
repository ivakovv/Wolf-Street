package com.example.user_service.service.interfaces;

import com.example.user_service.dto.auth.AuthenticationResponseDto;
import com.example.user_service.dto.auth.LoginRequestDto;
import com.example.user_service.dto.auth.RegistrationRequestDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

public interface AuthenticationService {
    void register(RegistrationRequestDto request);
    AuthenticationResponseDto authenticate(LoginRequestDto request);
    ResponseEntity<AuthenticationResponseDto> refreshToken(HttpServletRequest request);
}
