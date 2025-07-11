package com.example.market_data_service.service;

import com.example.market_data_service.dto.Order;
import com.example.market_data_service.dto.enums.OrderType;
import com.example.market_data_service.dto.orderbook.AggregatedOrderBookLevel;
import com.example.market_data_service.dto.orderbook.OrderBookEntry;
import com.example.market_data_service.service.interfaces.RedisOrderBookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisOrderBookServiceImpl implements RedisOrderBookService {
    private final RedisTemplate<String, OrderBookEntry> orderBookRedisTemplate;
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public void addToOrderBook(Order order) {
        String zsetKey = getRedisKeyForOderBook(order.instrumentId(), order.type());
        String hashKey = getRedisKeyForOrderHash(order.instrumentId(), order.type());
        String orderId = order.orderId().toString();
        OrderBookEntry entry = new OrderBookEntry(order.orderId(), order.count(), Double.parseDouble(order.lotPrice().toString()), order.portfolioId());
        stringRedisTemplate.opsForZSet().add(zsetKey, orderId, entry.price());
        orderBookRedisTemplate.opsForHash().put(hashKey, orderId, entry);
    }

    @Override
    public void removeFromOrderBook(Long orderId, OrderType orderType, Long instrumentId) {
        String zsetKey = getRedisKeyForOderBook(instrumentId, orderType);
        String hashKey = getRedisKeyForOrderHash(instrumentId, orderType);
        Long deleted = stringRedisTemplate.opsForZSet().remove(zsetKey, orderId.toString());
        orderBookRedisTemplate.opsForHash().delete(hashKey, orderId.toString());
        if (deleted != null && deleted > 0) {
            log.info("Order from order book successfully deleted!");
        } else {
            log.error("Deleting order failed =(");
        }
    }

    @Override
    public void addOrderLevel(Long instrumentId, OrderType type, double price, long count) {
        String key = getRedisKeyForOrderLevels(instrumentId, type);
        stringRedisTemplate.opsForZSet().incrementScore(key, String.valueOf(price), count);
    }

    @Override
    public void removeOrderLevel(Long instrumentId, OrderType type, double price, long count) {
        String key = getRedisKeyForOrderLevels(instrumentId, type);
        Double newScore = stringRedisTemplate.opsForZSet().incrementScore(key, String.valueOf(price), -count);
        if (newScore != null && newScore <= 0) {
            log.info("Removing order book level: {}, instrument: {}", price, instrumentId);
            stringRedisTemplate.opsForZSet().remove(key, String.valueOf(price));
        }
    }

    @Override
    public List<AggregatedOrderBookLevel> getAggregatedLevels(Long instrumentId, OrderType type, long limit) {
        String key = getRedisKeyForOrderLevels(instrumentId, type);
        Set<ZSetOperations.TypedTuple<String>> levels;
        if (type == OrderType.BUY) {
            levels = stringRedisTemplate.opsForZSet().reverseRangeWithScores(key, 0, limit - 1);
        } else {
            levels = stringRedisTemplate.opsForZSet().rangeWithScores(key, 0, limit - 1);
        }
        if (levels == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Order book for instrument with id: %d doesn't exists", instrumentId));
        }
        return levels.stream()
                .filter(tuple -> tuple.getValue() != null && tuple.getScore() != null)
                .map(tuple -> new AggregatedOrderBookLevel(
                        new BigDecimal(tuple.getValue()),
                        tuple.getScore().longValue()
                ))
                .toList();
    }

    @Override
    public List<OrderBookEntry> getTopNOrders(OrderType orderType, Long instrumentId, Long ordersLimit) {
        String zsetKey = getRedisKeyForOderBook(instrumentId, orderType);
        String hashKey = getRedisKeyForOrderHash(instrumentId, orderType);
        Set<String> orderIds;
        switch (orderType) {
            case BUY -> orderIds = stringRedisTemplate.opsForZSet().reverseRange(zsetKey, 0, ordersLimit - 1);
            case SALE -> orderIds = stringRedisTemplate.opsForZSet().range(zsetKey, 0, ordersLimit - 1);
            default -> throw new IllegalArgumentException(String.format("Unknown order type: %s", orderType));
        }
        if (orderIds == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Order book for instrument with id: %d doesn't exists", instrumentId));
        }
        return orderIds.stream()
                .map(orderId -> (OrderBookEntry) orderBookRedisTemplate.opsForHash().get(hashKey, orderId))
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    public OrderBookEntry getBestBid(Long instrumentId) {
        String zsetKey = getRedisKeyForOderBook(instrumentId, OrderType.BUY);
        String hashKey = getRedisKeyForOrderHash(instrumentId, OrderType.BUY);
        Set<String> bestId = stringRedisTemplate.opsForZSet().reverseRange(zsetKey, 0, 0);
        if (bestId == null || bestId.isEmpty()) return null;
        String orderId = bestId.iterator().next();
        return (OrderBookEntry) orderBookRedisTemplate.opsForHash().get(hashKey, orderId);
    }

    @Override
    public OrderBookEntry getBestAsk(Long instrumentId) {
        String zsetKey = getRedisKeyForOderBook(instrumentId, OrderType.SALE);
        String hashKey = getRedisKeyForOrderHash(instrumentId, OrderType.SALE);
        Set<String> bestId = stringRedisTemplate.opsForZSet().range(zsetKey, 0, 0);
        if (bestId == null || bestId.isEmpty()) return null;
        String orderId = bestId.iterator().next();
        return (OrderBookEntry) orderBookRedisTemplate.opsForHash().get(hashKey, orderId);
    }

    private String getRedisKeyForOderBook(Long instrumentId, OrderType orderType) {
        return String.format("orderbook:%d:%s", instrumentId, orderType);
    }

    private String getRedisKeyForOrderHash(Long instrumentId, OrderType orderType) {
        return String.format("orderbook:orders:%d:%s", instrumentId, orderType);
    }

    private String getRedisKeyForOrderLevels(Long instrumentId, OrderType orderType) {
        return String.format("orderbook:%d:%s:levels", instrumentId, orderType);
    }
}
