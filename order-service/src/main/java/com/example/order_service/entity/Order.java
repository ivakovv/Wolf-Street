package com.example.order_service.entity;

import com.example.order_service.enums.OrderStatus;
import com.example.order_service.enums.OrderType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name="orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "portfolio_id")
    private Long portfolioId;

    @Column(name = "instrument_name")
    private String instrument_name;

    @Column(name = "count")
    private Double count;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private OrderType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private OrderStatus status;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime created_at;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updated_at;

}
