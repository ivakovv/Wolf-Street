package com.example.market_data_service.service;

import com.example.market_data_service.dto.Order;
import com.example.market_data_service.dto.enums.OrderType;
import com.example.market_data_service.dto.orderbook.OrderBookEntry;
import com.example.market_data_service.service.interfaces.RedisOrderBookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisOrderBookServiceImpl implements RedisOrderBookService {
    private final RedisTemplate<String, OrderBookEntry> redisTemplate;

    @Override
    public void addToOrderBook(Order order) {
        String key = getRedisKey(order.instrumentId(), order.type());
        OrderBookEntry entry = new OrderBookEntry(order.orderId(), order.count(), Double.parseDouble(order.lotPrice().toString()), order.portfolioId());
        redisTemplate.opsForZSet().add(key, entry, entry.price());
    }

    @Override
    public void removeFromOrderBook(Long orderId) {

    }

    @Override
    public List<OrderBookEntry> getTopNOrders(OrderType orderType, Long instrumentId, Long ordersLimit){
        String key = getRedisKey(instrumentId, orderType);
        Set<OrderBookEntry> orderBookEntries;
        switch (orderType){
            case BUY -> orderBookEntries = redisTemplate.opsForZSet().reverseRange(key, 0, ordersLimit - 1);
            case SALE -> orderBookEntries = redisTemplate.opsForZSet().range(key, 0, ordersLimit - 1);
            default -> throw new IllegalArgumentException(String.format("Unknown order type: %s", orderType));
        }
        if (orderBookEntries == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Order book for instrument with id: %d doesn't exists", instrumentId));
        }
        return orderBookEntries.stream().toList();
    }

    private String getRedisKey(Long instrumentId, OrderType orderType) {
        return String.format("orderbook:%d:%s", instrumentId, orderType);
    }
}
