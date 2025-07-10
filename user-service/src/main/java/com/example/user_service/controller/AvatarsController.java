package com.example.user_service.controller;

import com.example.user_service.service.StorageService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/storage")
public class AvatarsController {
    private final StorageService storageService;

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Фото успешно получено"),
            @ApiResponse(responseCode = "403", description = "Пользователь не авторизован"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @GetMapping("/users/avatars")
    public ResponseEntity<String> getAvatar(Authentication authentication){
        return ResponseEntity.ok(storageService.getUserAvatar(authentication));
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Фото успешно получены"),
            @ApiResponse(responseCode = "403", description = "Пользователь не авторизован"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @GetMapping("/users/avatars/batch")
    public ResponseEntity<Map<String, String>> getAvatars(@RequestParam Long[] usersId){
        return ResponseEntity.ok(storageService.getUsersAvatars(usersId));
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Фото успешно загружено"),
            @ApiResponse(responseCode = "400", description = "Недопустимый формат файла"),
            @ApiResponse(responseCode = "403", description = "Пользователь не авторизован"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @PutMapping(value = "/users/avatars", consumes = MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> loadAvatar(Authentication authentication, @RequestParam MultipartFile avatar) throws IOException {
        if (avatar.getSize() > 20971520L) {
            return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                    .body("Размер фото не должен превышать 20 МБ");
        }
        String contentType = avatar.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                    .body("Разрешены только файлы изображений (JPEG, PNG и т.д.)");
        }
        storageService.uploadAvatar(authentication, avatar);
        return ResponseEntity.ok().build();
    }


}
