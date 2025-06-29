package com.example.order_service.entity;

import com.example.order_service.enums.OrderStatus;
import com.example.order_service.enums.OrderType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name="orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long order_id;

    @Column(name = "user_id")
    private Long user_id;

    @Column(name = "portfolio_id")
    private Long portfolio_id;

    @Column(name = "instrument_name")
    private String instrument_name;

    @Column(name = "count")
    private Double count;

    @Column(name = "executed_count")
    private Double executed_count;

    @Column(name = "total")
    private Double total;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private OrderType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private OrderStatus status;

    @Column(name = "created_at")
    @CreationTimestamp
    private OffsetDateTime created_at;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private OffsetDateTime updated_at;

}
