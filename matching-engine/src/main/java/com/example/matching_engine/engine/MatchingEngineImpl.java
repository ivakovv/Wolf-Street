package com.example.matching_engine.engine;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import com.example.matching_engine.repository.OrderBookRedisRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import com.example.matching_engine.dto.Deal;
import com.example.matching_engine.dto.Order;
import com.example.matching_engine.dto.enums.OrderType;
import com.example.matching_engine.engine.interfaces.MatchingEngine;


@Component
@RequiredArgsConstructor
public class MatchingEngineImpl implements MatchingEngine {
    private final OrderBookRedisRepository orderBookRedisRepository;
    private final ConcurrentHashMap<Long, OrderBook> orderBooks = new ConcurrentHashMap<>();
    private final AtomicLong dealIdGenerator = new AtomicLong(1);

    @PostConstruct
    public void init(){
        restoreAllOrderBooks();
    }

    @Override
    public List<Deal> processOrder(Order order) {
        OrderBook orderBook = orderBooks.computeIfAbsent(order.instrumentId(), k -> new OrderBook());
        List<Deal> deals = new ArrayList<>();
        matchOrder(order, orderBook, deals);
        orderBookRedisRepository.saveOrderBook(order.instrumentId(), orderBook);
        return deals;
    }

    @Override
    public Order cancelOrder(Long orderId, OrderType type, Long instrumentId) {
        OrderBook orderBook = orderBooks.get(instrumentId);
        PriorityQueue<Order> queue = (type == OrderType.BUY) ? orderBook.getBids() : orderBook.getAsks();
        Order orderToRemove = queue.stream()
                .filter(order -> order.orderId().equals(orderId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("Order with id: %d doesn't exist", orderId)));
        queue.remove(orderToRemove);
        orderBookRedisRepository.saveOrderBook(orderToRemove.instrumentId(), orderBook);
        return orderToRemove;
    }

    private void matchOrder(Order order, OrderBook orderBook, List<Deal> deals) {
        long remains = order.count();
        while (remains > 0) {
            Optional<Order> bestOrderOpt = getBestOrder(order, orderBook);
            if (bestOrderOpt.isEmpty()) {
                break;
            }
            Order bestOrder = bestOrderOpt.get();
            if (!isPriceMatch(order, bestOrder)) {
                break;
            }
            pollBestOrder(order, orderBook);
            long dealQuantity = Math.min(remains, bestOrder.count());
            Deal deal = createDeal(order, bestOrder, dealQuantity, bestOrder.lotPrice());
            deals.add(deal);
            remains -= dealQuantity;
            long orderQuantityRemaining = bestOrder.count() - dealQuantity;
            if (orderQuantityRemaining > 0) {
                orderBook.addOrder(bestOrder.withCount(orderQuantityRemaining));
            }
        }
        if (remains > 0) {
            orderBook.addOrder(order.withCount(remains));
        }
    }

    private Optional<Order> getBestOrder(Order order, OrderBook orderBook) {
        return switch (order.type()) {
            case BUY -> orderBook.hasAsks() ? Optional.of(orderBook.getBestAsk()) : Optional.empty();
            case SALE -> orderBook.hasBids() ? Optional.of(orderBook.getBestBid()) : Optional.empty();
        };
    }

    private void pollBestOrder(Order order, OrderBook orderBook) {
        switch (order.type()) {
            case BUY -> orderBook.pollBestAsk();
            case SALE -> orderBook.pollBestBid();
        }
    }

    private boolean isPriceMatch(Order incoming, Order counter) {
        return switch (incoming.type()) {
            case BUY -> incoming.lotPrice().compareTo(counter.lotPrice()) >= 0;
            case SALE -> incoming.lotPrice().compareTo(counter.lotPrice()) <= 0;
        };
    }
    
    private Deal createDeal(Order incomingOrder, Order counterOrder, long count, BigDecimal price) {
        Order buyOrder = incomingOrder.type() == OrderType.BUY ? incomingOrder : counterOrder;
        Order saleOrder = incomingOrder.type() == OrderType.SALE ? incomingOrder : counterOrder;
        Long dealId = dealIdGenerator.getAndIncrement();
        return new Deal(dealId,
                buyOrder.orderId(),
                saleOrder.orderId(),
                buyOrder.portfolioId(),
                saleOrder.portfolioId(),
                buyOrder.instrumentId(),
                count,
                price
        );
    }

    private void restoreAllOrderBooks() {
        Set<String> keys = orderBookRedisRepository.getAllOrderBookKeys();
        for (String key : keys) {
            Long instrumentId = Long.valueOf(key.replace("orderbook:", ""));
            OrderBook orderBook = orderBookRedisRepository.loadOrderBook(instrumentId);
            orderBooks.put(instrumentId, orderBook);
        }
    }
}