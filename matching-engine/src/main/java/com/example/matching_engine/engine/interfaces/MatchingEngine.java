package com.example.matching_engine.engine.interfaces;

import com.example.matching_engine.dto.Deal;
import com.example.matching_engine.entity.Order;

import java.util.List;

public interface MatchingEngine {
    List<Deal> processOrder(Order order);
    Order cancelOrder(Long orderId);
}
