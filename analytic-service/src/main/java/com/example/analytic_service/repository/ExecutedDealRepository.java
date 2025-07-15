package com.example.analytic_service.repository;

import com.example.analytic_service.entity.ExecutedDeal;
import com.example.analytic_service.enums.OrderType;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    public Map<Long, BigDecimal> getInstrumentProfitability(List<Long> instrumentIds, String period) {
        String dateCondition;
        switch (period) {
            case "1 day":
                dateCondition = "created_at >= now() - INTERVAL 1 DAY";
                break;
            case "1 week":
                dateCondition = "created_at >= now() - INTERVAL 1 WEEK";
                break;
            case "1 month":
                dateCondition = "created_at >= now() - INTERVAL 1 MONTH";
                break;
            default:
                throw new IllegalArgumentException("Invalid period: " + period);
        }

        String sql = """
            WITH instrument_prices AS (
                SELECT 
                    instrument_id,
                    order_type,
                    lot_price,
                    created_at,
                    row_number() OVER (PARTITION BY instrument_id, order_type ORDER BY created_at DESC) as rn
                FROM executed_deals
                WHERE (? OR instrument_id IN (%s)) AND %s
            ),
            latest_buys AS (
                SELECT instrument_id, lot_price as buy_price
                FROM instrument_prices
                WHERE order_type = 'BUY' AND rn = 1
            ),
            latest_sales AS (
                SELECT instrument_id, lot_price as sale_price
                FROM instrument_prices
                WHERE order_type = 'SALE' AND rn = 1
            )
            SELECT 
                COALESCE(b.instrument_id, s.instrument_id) as instrument_id,
                CASE 
                    WHEN b.buy_price IS NOT NULL AND s.sale_price IS NOT NULL 
                    THEN ((s.sale_price - b.buy_price) / b.buy_price) * 100
                    ELSE 0
                END as profitability
            FROM latest_buys b
            FULL OUTER JOIN latest_sales s ON b.instrument_id = s.instrument_id
            """;

        String instrumentIdsPlaceholder = instrumentIds.isEmpty() ? "" :
                instrumentIds.stream()
                        .map(String::valueOf)
                        .collect(Collectors.joining(","));

        Map<Long, BigDecimal> profitMap = jdbcTemplate.query(
                String.format(sql, instrumentIdsPlaceholder, dateCondition),
                new Object[]{instrumentIds.isEmpty()},
                (rs, rowNum) -> Map.entry(
                        rs.getLong("instrument_id"),
                        rs.getBigDecimal("profitability").setScale(2, RoundingMode.HALF_UP)
                )
        ).stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        if (!instrumentIds.isEmpty()) {
            instrumentIds.forEach(id -> profitMap.putIfAbsent(id, BigDecimal.ZERO.setScale(2)));
        }

        return profitMap;
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
