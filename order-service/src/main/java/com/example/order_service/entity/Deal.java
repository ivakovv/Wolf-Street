package com.example.order_service.entity;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class Deal {
    private Long dealId;
    private Long buyOrderId;
    private Long saleOrderId;
    private Long buyPortfolioId;
    private Long salePortfolioId;
    private Long count;
    private BigDecimal lotPrice;
    private Long instrumentId;
    private Long buyerId;
    private Long sellerId;
}
