package com.example.portfolio_service.dto;

import java.math.BigDecimal;

public record PortfolioValueResponseDto(BigDecimal instrumentsValue, BigDecimal cashAmount, BigDecimal totalAmount) {
}
