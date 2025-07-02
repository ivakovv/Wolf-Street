package com.example.portfolio_service.dto;

public record PortfolioInstrumentResponseDto(Long instrumentId, Long availableAmount, Long blockedAmount, Long totalAmount) {
}
