package com.example.market_data_service.dto.orderbook;

import java.util.List;

public record OrderBookResponse(Long instrumentId, List<OrderBookEntry> bids, List<OrderBookEntry> asks) {
}
