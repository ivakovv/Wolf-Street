package com.example.market_data_service.service.interfaces;

import com.example.market_data_service.dto.Order;
import com.example.market_data_service.dto.enums.OrderType;
import com.example.market_data_service.dto.orderbook.OrderBookEntry;

import java.util.List;

public interface RedisOrderBookService {
    void addToOrderBook(Order order);
    void removeFromOrderBook(Long orderId);
    List<OrderBookEntry> getTopNOrders(OrderType orderType, Long instrumentId, Long ordersLimit);
}
