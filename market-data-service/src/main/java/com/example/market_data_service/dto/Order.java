package com.example.market_data_service.dto;


import com.example.market_data_service.dto.enums.OrderStatus;
import com.example.market_data_service.dto.enums.OrderType;

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
                    OffsetDateTime createdAt) {}