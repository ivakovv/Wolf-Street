package com.example.market_data_service.service;

import com.example.market_data_service.dto.Deal;
import com.example.market_data_service.dto.enums.Interval;
import com.example.market_data_service.dto.ohlc.Ohlc;
import com.example.market_data_service.service.interfaces.RedisOhlcService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class RedisOhlcServiceImpl implements RedisOhlcService {
    private final RedisTemplate<String, Ohlc> ohlcRedisTemplate;

    @Override
    public void processDeal(Deal deal) {
        for (Interval interval : Interval.values()) {
            log.info("Processing ohlc for deal interval: {}", interval.toString());
            OffsetDateTime openTime = truncateToInterval(deal.createdAt(), interval.getDuration());
            long epochSeconds = openTime.toEpochSecond();
            String redisKey = getOhlcRedisKey(deal.instrumentId(), interval);
            ZSetOperations<String, Ohlc> zSetOps = ohlcRedisTemplate.opsForZSet();
            Set<Ohlc> set = zSetOps.rangeByScore(redisKey, epochSeconds, epochSeconds);
            Ohlc ohlc = (set != null && !set.isEmpty()) ? set.iterator().next()
                    : new Ohlc(deal.lotPrice(),
                    deal.lotPrice(),
                    deal.lotPrice(),
                    deal.lotPrice(),
                    deal.count(),
                    openTime
            );
            Ohlc updated = updateOhlc(ohlc, deal);
            zSetOps.remove(redisKey, ohlc);
            zSetOps.add(redisKey, updated, epochSeconds);
        }
    }

    public List<Ohlc> getOhlc(Long instrumentId, Interval interval, OffsetDateTime from, OffsetDateTime to) {
        String redisKey = getOhlcRedisKey(instrumentId, interval);
        long fromEpoch = from.toEpochSecond();
        long toEpoch = to.toEpochSecond();
        Set<Ohlc> results = ohlcRedisTemplate.opsForZSet().rangeByScore(redisKey, fromEpoch, toEpoch);
        if (results == null) return List.of();
        return new ArrayList<>(results);
    }

    private OffsetDateTime truncateToInterval(OffsetDateTime time, Duration interval) {
        long seconds = interval.getSeconds();
        long epoch = time.toEpochSecond();
        long truncated = (epoch / seconds) * seconds;
        return OffsetDateTime.ofInstant(Instant.ofEpochSecond(truncated), time.getOffset());
    }

    private Ohlc updateOhlc(Ohlc ohlc, Deal deal) {
        return new Ohlc(
                ohlc.open(),
                ohlc.high().max(deal.lotPrice()),
                ohlc.low().min(deal.lotPrice()),
                deal.lotPrice(),
                ohlc.volume() + deal.count(),
                ohlc.openTime()
        );
    }

    private String getOhlcRedisKey(Long instrumentId, Interval interval) {
        return String.format("ohlc:%d:%s", instrumentId, interval.name().toLowerCase());
    }
}
