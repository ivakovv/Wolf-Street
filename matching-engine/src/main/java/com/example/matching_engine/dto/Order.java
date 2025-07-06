package com.example.matching_engine.dto;

import com.example.matching_engine.dto.enums.OrderStatus;
import com.example.matching_engine.dto.enums.OrderType;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record Order(Long orderId,
                    Long userId,
                    Long portfolioId,
                    Long instrumentId,
                    Long count,
                    BigDecimal lotPrice,
                    OrderType type,
                    OrderStatus status,
                    OffsetDateTime createdAt) {
}
