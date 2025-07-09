package com.example.portfolio_service.dto;

import java.math.BigDecimal;

public record CashRequestDto(String currency, BigDecimal amount) {
}
