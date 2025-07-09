package com.example.portfolio_service.dto;

import java.math.BigDecimal;

public record PortfolioCashResponseDto(String currency, BigDecimal availableAmount, BigDecimal blockedAmount, BigDecimal totalAmount) {
}
