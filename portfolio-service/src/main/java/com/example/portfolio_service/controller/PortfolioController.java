package com.example.portfolio_service.controller;

import com.example.portfolio_service.dto.CashRequestDto;
import com.example.portfolio_service.dto.InstrumentRequest;
import com.example.portfolio_service.dto.PortfolioCashResponseDto;
import com.example.portfolio_service.dto.PortfolioHistoryResponseDto;
import com.example.portfolio_service.dto.PortfolioInstrumentResponseDto;
import com.example.portfolio_service.dto.PortfolioResponseDto;
import com.example.portfolio_service.dto.PortfolioValueResponseDto;
import com.example.portfolio_service.dto.profitability.PortfolioProfitabilityRequest;
import com.example.portfolio_service.dto.profitability.PortfolioProfitabilityResponse;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/portfolio")
@RequiredArgsConstructor
public class PortfolioController {
    private final PortfolioService portfolioService;

    @GetMapping
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Портфель успешно получен!"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован!"),
            @ApiResponse(responseCode = "404", description = "Портфель пользователя не найден!"),
    })
    public ResponseEntity<PortfolioResponseDto> getPortfolio(Authentication authentication){
        return ResponseEntity.ok(portfolioService.getPortfolioForUser(authentication));
    }

    @GetMapping("/cash")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Средства успешно получены!"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован!"),
            @ApiResponse(responseCode = "404", description = "Портфель пользователя не найден!"),
    })
    public ResponseEntity<List<PortfolioCashResponseDto>> getCash(Authentication authentication) {
        return ResponseEntity.ok(portfolioService.getCashForUser(authentication));
    }

    @PostMapping("/cash")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Средства успешно зачислены!"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован!"),
            @ApiResponse(responseCode = "404", description = "Портфель пользователя не найден!"),
    })
    public ResponseEntity<PortfolioCashResponseDto> addCash(Authentication authentication, @RequestBody CashRequestDto request) {
        return ResponseEntity.ok(portfolioService.addCash(authentication, request));
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

    @GetMapping("/value")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Стоимость инструментов портфеля успешно получена!"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован!"),
            @ApiResponse(responseCode = "404", description = "Портфель пользователя не найден!"),
    })
    public ResponseEntity<List<PortfolioValueResponseDto>> getCurrentPortfolioValue(Authentication authentication) {
        return ResponseEntity.ok(portfolioService.getCurrentPortfolioValue(authentication));
    }

    @GetMapping("/history")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "История сделок успешно получена!"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован!"),
            @ApiResponse(responseCode = "404", description = "Портфель пользователя не найден!"),
    })
    public ResponseEntity<List<PortfolioHistoryResponseDto>> getPortfolioHistory(
            Authentication authentication,
            @RequestParam Long from,
            @RequestParam Long to) {
        {
            return ResponseEntity.ok(portfolioService.getPortfolioHistory(authentication, from, to));
        }
    }

    @GetMapping("/profitability")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Доходность портфеля успешно получена!"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован!"),
            @ApiResponse(responseCode = "404", description = "Портфель пользователя не найден!"),
    })
    public ResponseEntity<PortfolioProfitabilityResponse> getProfitability(
            @RequestBody PortfolioProfitabilityRequest request,
            Authentication authentication){
        return ResponseEntity.ok(portfolioService.getProfitability(request, authentication));
    }
}
