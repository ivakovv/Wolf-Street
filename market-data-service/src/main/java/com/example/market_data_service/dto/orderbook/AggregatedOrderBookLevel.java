package com.example.market_data_service.dto.orderbook;

import java.math.BigDecimal;

public record AggregatedOrderBookLevel(BigDecimal lotPrice, Long totalCount) {
}
