package com.example.portfolio_service.controller;

import com.example.portfolio_service.dto.InstrumentRequest;
import com.example.portfolio_service.dto.PortfolioCashResponseDto;
import com.example.portfolio_service.dto.PortfolioInstrumentResponseDto;
import com.example.portfolio_service.service.interfaces.PortfolioService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/portfolio")
@RequiredArgsConstructor
public class PortfolioController {
    private final PortfolioService portfolioService;

    @GetMapping("/cash")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Средства успешно получены!"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован!"),
            @ApiResponse(responseCode = "404", description = "Портфель пользователя не найден!"),
    })
    public ResponseEntity<List<PortfolioCashResponseDto>> getCash(Authentication authentication) {
        return ResponseEntity.ok(portfolioService.getCashForUser(authentication));
    }

    @GetMapping("/instruments")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Инструменты успешно получены!"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован!"),
            @ApiResponse(responseCode = "404", description = "Портфель пользователя не найден!"),
    })
    public ResponseEntity<List<PortfolioInstrumentResponseDto>> getInstruments(Authentication authentication) {
        return ResponseEntity.ok(portfolioService.getInstrumentsForUser(authentication));
    }

    @PostMapping("/instruments")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Инструмент успешно добавлен!"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован!"),
            @ApiResponse(responseCode = "404", description = "Портфель пользователя не найден!"),
    })
    public ResponseEntity<Void> addInstrument(Authentication authentication, @RequestBody InstrumentRequest request) {
        portfolioService.addInstrumentToPortfolio(authentication, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/instruments")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Инструмент успешно удален!"),
            @ApiResponse(responseCode = "400", description = "Пользователь содержит позиции инструмента!"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован!"),
            @ApiResponse(responseCode = "404", description = "Портфель пользователя или инструмент не найден!"),
    })
    public ResponseEntity<Void> removeInstrument(Authentication authentication, @RequestBody InstrumentRequest request) {
        portfolioService.removeInstrumentFromPortfolio(authentication, request);
        return ResponseEntity.ok().build();
    }
}
