package com.example.market_data_service.service.interfaces;

import com.example.market_data_service.dto.orderbook.OrderBookAggregatedResponse;
import com.example.market_data_service.dto.orderbook.OrderBookResponse;

public interface OrderBookService {
    OrderBookResponse getOrderBook(Long instrumentId, Long limitOrders);

    OrderBookAggregatedResponse getAggregatedOrderBook(Long instrumentId, Long limitLevels);
}
