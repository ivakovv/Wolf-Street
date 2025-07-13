package com.example.analytic_service.entity;

import com.example.analytic_service.enums.OrderType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExecutedDeal {
    private long orderId;
    private long portfolioId;
    private long count;
    private BigDecimal lotPrice;
    private long instrumentId;
    private OrderType orderType;
    private OffsetDateTime createdAt;
}
