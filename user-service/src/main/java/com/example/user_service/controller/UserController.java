package com.example.user_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.user_service.dto.user.UserResponseDto;
import com.example.user_service.dto.user.UserUpdateDto;
import com.example.user_service.service.interfaces.UserService;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    @ApiResponses(value ={
            @ApiResponse(responseCode = "200", description = "Пользователь успешно получен!"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован!"),
            @ApiResponse(responseCode = "403", description = "Нет доступа к ресурсу!"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден!"),
    })
    public ResponseEntity<UserResponseDto> getCurrentUser(Authentication authentication){
        return ResponseEntity.ok(userService.getCurrentUser(authentication));
    }

    @PutMapping("/me")
    @ApiResponses(value ={
            @ApiResponse(responseCode = "200", description = "Пользователь успешно обновлен!"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован!"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден!"),
    })
    public ResponseEntity<UserResponseDto> updateUser(@RequestBody UserUpdateDto userUpdateDto, Authentication authentication){
        return ResponseEntity.ok(userService.updateUser(userUpdateDto, authentication));
    }
}
