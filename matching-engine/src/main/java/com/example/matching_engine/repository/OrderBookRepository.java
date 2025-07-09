package com.example.matching_engine.repository;

import com.example.matching_engine.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface OrderBookRepository extends JpaRepository<Order, Long> {

    @Query(value = """
            SELECT * FROM order_book
            WHERE instrument_id = :instrumentId AND portfolio_id != :portfolioId AND order_type = 'SALE'
              AND lot_price <= :lotPrice AND order_status != 'CANCELLED' AND order_status != 'EXECUTED'
            ORDER BY lot_price ASC, created_at ASC
            FOR UPDATE SKIP LOCKED
            """, nativeQuery = true)
    List<Order> findSellOrdersForBuy(
            @Param("portfolioId") Long portfolioId,
            @Param("instrumentId") Long instrumentId,
            @Param("lotPrice") BigDecimal lotPrice);

    @Query(value = """
            SELECT * FROM order_book
                WHERE instrument_id = :instrumentId AND portfolio_id != :portfolioId AND order_type = 'BUY'
                 AND lot_price >= :lotPrice AND order_status != 'CANCELLED' AND order_status != 'EXECUTED'
            ORDER BY lot_price DESC, created_at ASC
            FOR UPDATE SKIP LOCKED
            """, nativeQuery = true)
    List<Order> findBuyOrdersForSell(
            @Param("portfolioId") Long portfolioId,
            @Param("instrumentId") Long instrumentId,
            @Param("lotPrice") BigDecimal lotPrice
    );

}
