package com.example.user_service.service.interfaces;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import com.example.user_service.dto.auth.AuthenticationResponseDto;
import com.example.user_service.dto.auth.ChangePasswordRequestDto;
import com.example.user_service.dto.auth.LoginRequestDto;
import com.example.user_service.dto.auth.RegistrationRequestDto;

import jakarta.servlet.http.HttpServletRequest;

public interface AuthenticationService {
    void register(RegistrationRequestDto request);
    AuthenticationResponseDto authenticate(LoginRequestDto request);
    ResponseEntity<AuthenticationResponseDto> refreshToken(HttpServletRequest request);
    void changePassword(ChangePasswordRequestDto request, Authentication authentication);
}
