package com.example.order_service.dto;

import com.example.order_service.enums.OrderType;

public record CreateRequestDto(
        Long user_id,
        Long portfolio_id,
        Double total,
        Double executed_count,
        String instrument_name,
        Double count,
        OrderType type) {
}
