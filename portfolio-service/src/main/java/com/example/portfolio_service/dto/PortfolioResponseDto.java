package com.example.portfolio_service.dto;

import com.example.portfolio_service.entity.Portfolio;

import java.time.LocalDateTime;

public record PortfolioResponseDto(Long portfolioId, Long userId, LocalDateTime createdAt) {
    public static PortfolioResponseDto fromPortfolio(Portfolio portfolio) {
        return new PortfolioResponseDto(portfolio.getId(), portfolio.getUserId(), portfolio.getCreatedAt());
    }
}
