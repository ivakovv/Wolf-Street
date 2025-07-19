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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.ArrayList;
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
            log.info("Order: {} from order book successfully deleted!", orderId);
        } else {
            log.error("Deleting order {} failed =(", orderId);
        }
    }

    @Override
    public void addOrderLevel(Long instrumentId, OrderType type, double price, long count) {
        String zsetKey = getRedisKeyForOrderLevels(instrumentId, type);
        String hashKey = getRedisKeyForOrderLevelsHash(instrumentId, type);
        String priceStr = new BigDecimal(price).toPlainString();
        stringRedisTemplate.opsForHash().increment(hashKey, priceStr, count);
        stringRedisTemplate.opsForZSet().add(zsetKey, priceStr, price);
    }

    @Override
    public void removeOrderLevel(Long instrumentId, OrderType type, double price, long count) {
        String zsetKey = getRedisKeyForOrderLevels(instrumentId, type);
        String hashKey = getRedisKeyForOrderLevelsHash(instrumentId, type);
        String priceStr = new BigDecimal(price).toPlainString();
        Long newCount = stringRedisTemplate.opsForHash().increment(hashKey, priceStr, -count);
        if (newCount <= 0) {
            log.info("Removing order book level: {}, instrument: {}, side: {}", price, instrumentId, type);
            stringRedisTemplate.opsForHash().delete(hashKey, priceStr);
            stringRedisTemplate.opsForZSet().remove(zsetKey, priceStr);
        }
    }

    @Override
    public List<AggregatedOrderBookLevel> getAggregatedLevels(Long instrumentId, OrderType type, long limit) {
        String zsetKey = getRedisKeyForOrderLevels(instrumentId, type);
        String hashKey = getRedisKeyForOrderLevelsHash(instrumentId, type);
        Set<String> prices;
        switch (type) {
            case BUY -> prices = stringRedisTemplate.opsForZSet().reverseRange(zsetKey, 0, limit - 1);
            case SALE -> prices = stringRedisTemplate.opsForZSet().range(zsetKey, 0, limit - 1);
            default -> throw new IllegalArgumentException(String.format("Unknown order type: %s", type));
        }
        if (prices == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Order book for instrument with id: %d doesn't exists", instrumentId));
        }
        List<String> priceList = new ArrayList<>(prices);
        List<Object> counts = stringRedisTemplate.opsForHash().multiGet(hashKey, new ArrayList<>(priceList));
        List<AggregatedOrderBookLevel> result = new ArrayList<>();
        for (int i = 0; i < priceList.size(); i++) {
            Object count = counts.get(i);
            if (count != null) {
                result.add(new AggregatedOrderBookLevel(
                        new BigDecimal(priceList.get(i)),
                        Long.parseLong(count.toString())
                ));
            }
        }
        return result;
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

    private String getRedisKeyForOrderLevelsHash(Long instrumentId, OrderType orderType) {
        return String.format("orderbook:%d:%s:counts", instrumentId, orderType);
    }
}
