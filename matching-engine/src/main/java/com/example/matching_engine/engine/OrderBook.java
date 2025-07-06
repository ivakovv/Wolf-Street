package com.example.matching_engine.engine;

import com.example.matching_engine.dto.Order;

import java.util.Comparator;
import java.util.PriorityQueue;

public class OrderBook {

    private final PriorityQueue<Order> bids = new PriorityQueue<>(
            Comparator.comparing(Order::lotPrice).reversed().thenComparing(Order::createdAt)
    );

    private final PriorityQueue<Order> asks = new PriorityQueue<>(
            Comparator.comparing(Order::lotPrice).thenComparing(Order::createdAt)
    );

    public void addOrder(Order order) {
        switch (order.type()) {
            case BUY -> bids.add(order);
            case SALE -> asks.add(order);
        }
    }

    public Order getBestBid() {
        return bids.peek();
    }

    public Order getBestAsk() {
        return asks.peek();
    }

    public Order pollBestBid() {
        return bids.poll();
    }

    public Order pollBestAsk() {
        return asks.poll();
    }

    public boolean hasBids() {
        return !bids.isEmpty();
    }

    public boolean hasAsks() {
        return !asks.isEmpty();
    }
}

