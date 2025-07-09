package com.example.order_service.dto;

import com.example.order_service.enums.OrderStatus;

public record OrderStatusResponseDto(OrderStatus status) {
}