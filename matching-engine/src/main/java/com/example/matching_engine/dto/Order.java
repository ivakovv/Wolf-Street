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
                    OffsetDateTime createdAt) implements Comparable<Order> {
    public Order withCount(long newCount) {
        return new Order(orderId(), userId(), portfolioId(), instrumentId(), newCount, lotPrice(), type(), status(), createdAt());
    }
    @Override
    public int compareTo(Order other) {
        int priceCmp = this.lotPrice().compareTo(other.lotPrice());
        if (priceCmp != 0) {
            return priceCmp;
        }
        return this.createdAt().compareTo(other.createdAt());
    }
}