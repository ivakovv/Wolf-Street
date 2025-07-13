package com.example.market_data_service.dto.orderbook;

import java.math.BigDecimal;

public record SpreadResponse(Long instrumentId, BigDecimal bestBid, BigDecimal bestAsk, BigDecimal spread, BigDecimal midPrice) {
}
