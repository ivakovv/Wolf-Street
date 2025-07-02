package com.example.order_service.dto;

import com.example.order_service.enums.OrderType;

import java.math.BigDecimal;

public record CreateRequestDto(
        Long portfolioId,
        BigDecimal lotPrice,
        Long instrumentId,
        Long count,
        OrderType type) {
}
