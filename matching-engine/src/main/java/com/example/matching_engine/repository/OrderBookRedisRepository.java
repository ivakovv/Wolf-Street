package com.example.matching_engine.repository;

import com.example.matching_engine.engine.OrderBook;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@Repository
@RequiredArgsConstructor
public class OrderBookRedisRepository {

    private final RedisTemplate<String, OrderBook> redisTemplate;
    private static final String ORDER_BOOK_KEY_PREFIX = "orderbook:";
    @Value("${matching-engine.snapshot.ttl-hours}")
    private int ttl_hours;

    public void saveOrderBook(Long instrumentId, OrderBook orderBook) {
        String key = ORDER_BOOK_KEY_PREFIX + instrumentId;
        try {
            orderBook.setLastSnapshotTime(OffsetDateTime.now());
            redisTemplate.opsForValue().set(key, orderBook, ttl_hours, TimeUnit.HOURS);
            log.info("Снапшот сохранен: instrumentId={}, bids={}, asks={}, totalBids={}, totalAsks={}",
                    instrumentId,
                    orderBook.getBidsSize(),
                    orderBook.getAsksSize(),
                    orderBook.getTotalBidsCount(),
                    orderBook.getTotalAsksCount());
        } catch (Exception e) {
            log.error("Ошибка сохранения снапшота: instrumentId={}, error={}", instrumentId, e.getMessage());
        }
    }

    public OrderBook loadOrderBook(Long instrumentId) {
        String key = ORDER_BOOK_KEY_PREFIX + instrumentId;
        try {
            OrderBook orderBook = redisTemplate.opsForValue().get(key);
            if (orderBook != null) {
                orderBook.setBids(orderBook.getBids());
                orderBook.setAsks(orderBook.getAsks());
                log.info("Загружен снапшот: instrumentId={}, bids={}, asks={}",
                        instrumentId, orderBook.getBidsSize(), orderBook.getAsksSize());
                return orderBook;
            }
        } catch (Exception e) {
            log.error("Ошибка загрузки снапшота: instrumentId={}, error={}", instrumentId, e.getMessage());
        }
        log.info("Снапшот не найден, создаем новый стакан: instrumentId={}", instrumentId);
        return new OrderBook(instrumentId);
    }

    public Set<String> getAllOrderBookKeys() {
        return redisTemplate.keys(ORDER_BOOK_KEY_PREFIX + "*");
    }

    public Long getNextDealId() {
        Long id = redisTemplate.opsForValue().increment("deal:lastId");
        if (id == null) {
            throw new IllegalStateException("Не удалось получить новый dealId из Redis");
        }
        return id;
    }

    public void deleteOrderBook(Long instrumentId) {
        String key = ORDER_BOOK_KEY_PREFIX + instrumentId;
        redisTemplate.delete(key);
        log.info("Удален снапшот: instrumentId={}", instrumentId);
    }

    public boolean existsOrderBook(Long instrumentId) {
        String key = ORDER_BOOK_KEY_PREFIX + instrumentId;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
}
