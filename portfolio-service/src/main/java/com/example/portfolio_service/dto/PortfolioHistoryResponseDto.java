package com.example.portfolio_service.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record PortfolioHistoryResponseDto(String dealType, Long instrumentId, Long count, BigDecimal lotPrice, BigDecimal totalAmount, OffsetDateTime completedAt) {
}
