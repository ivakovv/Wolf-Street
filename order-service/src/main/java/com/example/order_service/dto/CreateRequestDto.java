package com.example.order_service.dto;

import com.example.order_service.enums.OrderType;

public record CreateRequestDto(
        Long userId,
        Long portfolioId,
        String instrument_name,
        Double count,
        OrderType type) {
}
