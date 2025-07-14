package com.example.analytic_service.repository;

import com.example.analytic_service.entity.ExecutedDeal;
import com.example.analytic_service.enums.OrderType;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.ZoneOffset;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ExecutedDealRepository {

    private final JdbcTemplate jdbcTemplate;

    public void save(ExecutedDeal deal) {
        jdbcTemplate.update("""
            INSERT INTO executed_deals 
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

    public List<ExecutedDeal> getDeals(Long portfolioId, Long offset, Long limit) {
            String sql = """
            SELECT order_id, portfolio_id, count, lot_price, instrument_id, order_type, created_at
            FROM executed_deals
            WHERE portfolio_id = ?
            OFFSET ? LIMIT ?
            """;
        return jdbcTemplate.query(
                sql,
                new Object[]{portfolioId, offset, limit},
                (rs, rowNum) -> {
                    ExecutedDeal deal = new ExecutedDeal();
                    deal.setOrderId(rs.getLong("order_id"));
                    deal.setPortfolioId(rs.getLong("portfolio_id"));
                    deal.setCount(rs.getLong("count"));
                    deal.setLotPrice(rs.getBigDecimal("lot_price"));
                    deal.setInstrumentId(rs.getLong("instrument_id"));
                    deal.setOrderType(OrderType.valueOf(rs.getString("order_type")));
                    deal.setCreatedAt(rs.getTimestamp("created_at").toInstant()
                            .atOffset(ZoneOffset.UTC));
                    return deal;
                }
        );
    }

    public List<ExecutedDeal> findByPortfolioId(long portfolioId) {
        return jdbcTemplate.query("""
            SELECT * FROM executed_deals
            WHERE portfolio_id = ?
            ORDER BY created_at DESC
            """,
                new BeanPropertyRowMapper<>(ExecutedDeal.class),
                portfolioId
        );
    }
}
