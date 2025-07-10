package com.example.market_data_service.service.interfaces;

import com.example.market_data_service.dto.orderbook.OrderBookAggregatedResponse;
import com.example.market_data_service.dto.orderbook.OrderBookResponse;
import com.example.market_data_service.dto.orderbook.SpreadResponse;

public interface OrderBookService {
    OrderBookResponse getOrderBook(Long instrumentId, Long limitOrders);
    OrderBookAggregatedResponse getAggregatedOrderBook(Long instrumentId, Long limitLevels);
    SpreadResponse getSpread(Long instrumentId);
}
