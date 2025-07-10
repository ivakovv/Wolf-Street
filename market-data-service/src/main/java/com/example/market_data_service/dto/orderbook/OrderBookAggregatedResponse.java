package com.example.market_data_service.dto.orderbook;

import java.util.List;

public record OrderBookAggregatedResponse(Long instrumentId, List<AggregatedOrderBookLevel> bids, List<AggregatedOrderBookLevel> asks) {
}
