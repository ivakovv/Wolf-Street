package com.example.matching_engine.entity;

import com.example.matching_engine.dto.enums.OrderStatus;
import com.example.matching_engine.dto.enums.OrderType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "order_book")
public class Order {
    @Id
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "portfolio_id")
    private Long portfolioId;

    @Column(name = "instrument_id")
    private Long instrumentId;

    @Column(name = "count")
    private Long count;

    @Column(name = "lot_price")
    private BigDecimal lotPrice;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "order_type")
    private OrderType orderType;

    @Enumerated(value = EnumType.STRING)
    private OrderStatus orderStatus;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;
}
