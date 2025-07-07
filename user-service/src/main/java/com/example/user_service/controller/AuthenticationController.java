package com.example.user_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.user_service.dto.auth.AuthenticationResponseDto;
import com.example.user_service.dto.auth.ChangePasswordRequestDto;
import com.example.user_service.dto.auth.LoginRequestDto;
import com.example.user_service.dto.auth.RegistrationRequestDto;
import com.example.user_service.service.interfaces.AuthenticationService;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь успешно зарегистрирован!"),
            @ApiResponse(responseCode = "409", description = "Пользователь с таким email или username уже существует!"),
            @ApiResponse(responseCode = "500", description = "Сервер в данный момент не доступен!")
    })
    public ResponseEntity<Void> register(@RequestBody RegistrationRequestDto request){
        authenticationService.register(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь успешно зарегистрирован!"),
            @ApiResponse(responseCode = "403", description = "Неверное имя пользователя или пароль!"),
            @ApiResponse(responseCode = "500", description = "Сервер в данный момент не доступен!")
    })
    public ResponseEntity<AuthenticationResponseDto> login(@RequestBody LoginRequestDto request){
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }
    @PostMapping("/change-password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пароль успешно изменен!"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован!"),
            @ApiResponse(responseCode = "409", description = "Неверный текущий или новый пароль!"),
            @ApiResponse(responseCode = "500", description = "Сервер в данный момент не доступен!")
    })
    public ResponseEntity<Void> changePassword(@RequestBody ChangePasswordRequestDto request, Authentication authentication){
        authenticationService.changePassword(request, authentication);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/refresh_token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь успешно зарегистрирован!"),
            @ApiResponse(responseCode = "401", description = "Невалидный токен!"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден!"),
            @ApiResponse(responseCode = "500", description = "Сервер в данный момент не доступен!")
    })
    public ResponseEntity<AuthenticationResponseDto> refreshToken(HttpServletRequest request) {
        return authenticationService.refreshToken(request);
    }
}
