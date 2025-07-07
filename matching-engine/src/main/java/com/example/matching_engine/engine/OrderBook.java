package com.example.matching_engine.engine;

import com.example.matching_engine.dto.Order;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.PriorityQueue;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderBook {

    private PriorityQueue<Order> bids = new PriorityQueue<>(
            Comparator.comparing(Order::lotPrice).reversed().thenComparing(Order::createdAt)
    );

    private PriorityQueue<Order> asks = new PriorityQueue<>(
            Comparator.comparing(Order::lotPrice).thenComparing(Order::createdAt)
    );

    private Long instrumentId;
    private OffsetDateTime lastSnapshotTime;
    private long totalBidsCount;
    private long totalAsksCount;

    public OrderBook() {
        this.lastSnapshotTime = OffsetDateTime.now();
    }

    public OrderBook(Long instrumentId) {
        this();
        this.instrumentId = instrumentId;
    }

    public void addOrder(Order order) {
        switch (order.type()) {
            case BUY -> {
                bids.add(order);
                totalBidsCount++;
            }
            case SALE -> {
                asks.add(order);
                totalAsksCount++;
            }
        }
    }

    public Order getBestBid() {
        return bids.peek();
    }

    public Order getBestAsk() {
        return asks.peek();
    }

    public Order pollBestBid() {
        Order order = bids.poll();
        if (order != null) {
            totalBidsCount--;
        }
        return order;
    }

    public Order pollBestAsk() {
        Order order = asks.poll();
        if (order != null) {
            totalAsksCount--;
        }
        return order;
    }

    public void setBids(PriorityQueue<Order> bids) {
        this.bids = new PriorityQueue<>(Comparator.comparing(Order::lotPrice).reversed().thenComparing(Order::createdAt));
        this.bids.addAll(bids);
    }

    public void setAsks(PriorityQueue<Order> asks) {
        this.asks = new PriorityQueue<>(Comparator.comparing(Order::lotPrice).thenComparing(Order::createdAt));
        this.asks.addAll(asks);
    }

    public boolean hasBids() {
        return !bids.isEmpty();
    }

    public boolean hasAsks() {
        return !asks.isEmpty();
    }

    @JsonIgnore
    public int getBidsSize() {
        return bids.size();
    }

    @JsonIgnore
    public int getAsksSize() {
        return asks.size();
    }
}


