package com.example.analytic_service.repository;

import com.example.analytic_service.entity.CancelledDeal;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CancelledDealRepository {

    private final JdbcTemplate jdbcTemplate;

    public void save(CancelledDeal deal) {
        jdbcTemplate.update("""
            INSERT INTO cancelled_deals 
            (order_id, portfolio_id, count, lot_price, instrument_id, order_type, created_at)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """,
                deal.getOrderId(),
                deal.getPortfolioId(),
                deal.getCount(),
                deal.getLotPrice(),
                deal.getInstrumentId(),
                deal.getOrderType().name(),
                deal.getCreatedAt()
        );
    }

    public List<CancelledDeal> findByPortfolioId(long portfolioId) {
        return jdbcTemplate.query("""
            SELECT * FROM cancelled_deals
            WHERE portfolio_id = ?
            ORDER BY created_at DESC
            """,
                new BeanPropertyRowMapper<>(CancelledDeal.class),
                portfolioId
        );
    }
}
