package com.example.matching_engine.engine.interfaces;

import com.example.matching_engine.dto.Deal;
import com.example.matching_engine.dto.Order;
import com.example.matching_engine.dto.enums.OrderType;

import java.util.List;

public interface MatchingEngine {
    List<Deal> processOrder(Order order);
    Order cancelOrder(Long orderId, OrderType type, Long instrumentId);
}
