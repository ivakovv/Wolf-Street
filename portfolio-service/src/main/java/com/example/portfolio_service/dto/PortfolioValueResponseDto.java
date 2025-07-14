package com.example.portfolio_service.dto;

import java.math.BigDecimal;

public record PortfolioValueResponseDto(Long instrumentId, BigDecimal lotPrice, BigDecimal totalPrice) {
}
