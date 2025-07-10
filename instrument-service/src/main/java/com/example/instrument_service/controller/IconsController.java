package com.example.instrument_service.controller;

import com.example.instrument_service.service.StorageService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/storage")
public class IconsController {

    private final StorageService storageService;

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Фото успешно загружено"),
            @ApiResponse(responseCode = "400", description = "Недопустимый формат файла"),
            @ApiResponse(responseCode = "403", description = "Пользователь не авторизован"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @PutMapping(value = "/instruments/{instrument_id}", consumes = MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadInstrumentPhoto(@RequestParam MultipartFile icon, @PathVariable("instrument_id") Long instrumentId)
            throws IOException {
        if (icon.getSize() > 20971520L) {
            return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                    .body("Размер фото не должен превышать 20 МБ");
        }
        String contentType = icon.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                    .body("Разрешены только файлы изображений (JPEG, PNG и т.д.)");
        }
        storageService.uploadIcon(instrumentId, icon);
        return ResponseEntity.ok().build();
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Фото успешно получены"),
            @ApiResponse(responseCode = "403", description = "Пользователь не авторизован"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @GetMapping("/instruments/images")
    public ResponseEntity<Map<String, String>> downloadInstrumentsImages(
            @RequestParam Long[] instrumentIds) {
        return ResponseEntity.ok(storageService.getInstrumentsImages(instrumentIds));
    }
}
