package com.example.market_data_service.config;

import com.example.market_data_service.dto.Order;
import com.example.market_data_service.dto.enums.OrderStatus;
import com.example.market_data_service.dto.enums.OrderType;
import com.example.market_data_service.service.interfaces.RedisOrderBookService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Configuration
@RequiredArgsConstructor
public class RedisTestRunner implements CommandLineRunner {
    private final RedisOrderBookService redisOrderBookService;
    @Override
    public void run(String... args) throws Exception {
        Order order = new Order(3L, 1L, 1L, 30L, 100L, new BigDecimal("100.0"), OrderType.SALE, OrderStatus.NEW, OffsetDateTime.now());
        Order order1 = new Order(2L, 1L, 1L, 30L, 100L, new BigDecimal("150.0"), OrderType.SALE, OrderStatus.NEW, OffsetDateTime.now());
        Order order2 = new Order(2L, 1L, 1L, 30L, 120L, new BigDecimal("100.0"), OrderType.SALE, OrderStatus.NEW, OffsetDateTime.now());

        redisOrderBookService.addToOrderBook(order);
        redisOrderBookService.addToOrderBook(order1);
    }
}
