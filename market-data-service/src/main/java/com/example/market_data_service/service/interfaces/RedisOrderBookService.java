package com.example.market_data_service.service.interfaces;

import com.example.market_data_service.dto.Order;
import com.example.market_data_service.dto.enums.OrderType;
import com.example.market_data_service.dto.orderbook.AggregatedOrderBookLevel;
import com.example.market_data_service.dto.orderbook.OrderBookEntry;

import java.util.List;

public interface RedisOrderBookService {
    void addToOrderBook(Order order);
    void removeFromOrderBook(Long orderId, OrderType orderType, Long instrumentId);
    List<OrderBookEntry> getTopNOrders(OrderType orderType, Long instrumentId, Long ordersLimit);
    void addOrderLevel(Long instrumentId, OrderType type, double price, long count);
    void removeOrderLevel(Long instrumentId, OrderType type, double price, long count);
    List<AggregatedOrderBookLevel> getAggregatedLevels(Long instrumentId, OrderType type, long limit);
    OrderBookEntry getBestBid(Long instrumentId);
    OrderBookEntry getBestAsk(Long instrumentId);
}
