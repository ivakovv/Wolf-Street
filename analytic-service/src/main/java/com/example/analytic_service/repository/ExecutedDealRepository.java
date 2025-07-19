package com.example.analytic_service.repository;

import com.example.analytic_service.entity.ExecutedDeal;
import com.example.analytic_service.enums.OrderType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
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
            LIMIT ? OFFSET ?
            """;

        return jdbcTemplate.query(
                sql,
                new Object[]{portfolioId, limit, offset},
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
    public Map<Long, BigDecimal> calculateRealizedProfit(Long portfolioId, List<Long> instrumentIds) {
        if (instrumentIds == null || instrumentIds.isEmpty()) {
            throw new IllegalArgumentException("Instrument IDs list cannot be empty");
        }
        String sql = """
            WITH ranked_buys AS (
                SELECT 
                    instrument_id,
                    lot_price,
                    count,
                    created_at,
                    order_id,
                    row_number() OVER (PARTITION BY instrument_id ORDER BY created_at, order_id) as buy_rank
                FROM executed_deals
                WHERE order_type = 'BUY' AND portfolio_id = ?
            ),
            ranked_sales AS (
                SELECT 
                    instrument_id,
                    lot_price,
                    count,
                    created_at,
                    order_id,
                    row_number() OVER (PARTITION BY instrument_id ORDER BY created_at, order_id) as sale_rank
                FROM executed_deals
                WHERE order_type = 'SALE' AND portfolio_id = ?
            )
            SELECT 
                s.instrument_id,
                SUM((s.lot_price - b.lot_price) * b.count) AS realized_profit
            FROM ranked_sales s
            JOIN ranked_buys b ON 
                s.instrument_id = b.instrument_id AND
                b.buy_rank = s.sale_rank
            WHERE ? OR s.instrument_id IN (%s)
            GROUP BY s.instrument_id
            """;

        String instrumentIdsPlaceholder = instrumentIds.isEmpty() ? "" :
                instrumentIds.stream()
                        .map(String::valueOf)
                        .collect(Collectors.joining(","));

        Map<Long, BigDecimal> profitMap = jdbcTemplate.query(
                String.format(sql, instrumentIdsPlaceholder),
                new Object[]{ portfolioId, portfolioId, instrumentIds.isEmpty() },
                (rs, rowNum) -> Map.entry(
                        rs.getLong("instrument_id"),
                        rs.getBigDecimal("realized_profit")
                )
        ).stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        if (!instrumentIds.isEmpty()) {
            instrumentIds.forEach(id -> profitMap.putIfAbsent(id, BigDecimal.ZERO));
        }

        return profitMap;
    }
    
    public Map<Long, BigDecimal> getWeightedProfitability(List<Long> instrumentIds, String fromDate) {
        if (instrumentIds == null || instrumentIds.isEmpty()) {
            throw new IllegalArgumentException("Instrument IDs list cannot be empty");
        }
        String instrumentIdsList = instrumentIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(", ", "(", ")"));

        String sql = """
            SELECT instrument_id,
                   sumIf(lot_price * count, order_type = 'BUY') / nullIf(sumIf(count, order_type = 'BUY'), 0) AS weighted_avg_buy_price,
                   sumIf(lot_price * count, order_type = 'SALE') / nullIf(sumIf(count, order_type = 'SALE'), 0) AS weighted_avg_sale_price,
                   round((weighted_avg_sale_price - weighted_avg_buy_price) / weighted_avg_buy_price * 100, 2) AS profitability_percent
            FROM executed_deals
            WHERE instrument_id IN %s
              AND created_at >= ?
            GROUP BY instrument_id
        """;

        return jdbcTemplate.query(
                String.format(sql, instrumentIdsList),
                new Object[]{fromDate},
                (rs, rowNum) -> Map.entry(
                        rs.getLong("instrument_id"),
                        rs.getBigDecimal("profitability_percent")
                )
        ).stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
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
