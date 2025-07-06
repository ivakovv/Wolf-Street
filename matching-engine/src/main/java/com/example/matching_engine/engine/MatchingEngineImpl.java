package com.example.matching_engine.engine;

import com.example.matching_engine.dto.Deal;
import com.example.matching_engine.dto.Order;
import com.example.matching_engine.engine.interfaces.MatchingEngine;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MatchingEngineImpl implements MatchingEngine {
    private final ConcurrentHashMap<Long, OrderBook> orderBooks = new ConcurrentHashMap<>();

    @Override
    public List<Deal> processOrder(Order order) {
        OrderBook orderBook = orderBooks.computeIfAbsent(order.instrumentId(), k -> new OrderBook());
        List<Deal> deals = new ArrayList<>();
        switch (order.type()){
            case BUY -> processBuyOrder(order, orderBook, deals);
            case SALE -> processSaleOrder(order, orderBook, deals);
        }
        return deals;
    }

    private void processBuyOrder(Order order, OrderBook orderBook, List<Deal> deals){

    }
    private void processSaleOrder(Order order, OrderBook orderBook, List<Deal> deals){

    }
}
