package com.example.market_data_service.service;

import com.example.market_data_service.dto.Deal;
import com.example.market_data_service.dto.enums.Interval;
import com.example.market_data_service.dto.ohlc.Ohlc;
import com.example.market_data_service.service.interfaces.RedisOhlcService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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
            log.info("Time: {}", deal.createdAt());
            String redisKey = getOhlcRedisKey(deal.instrumentId(), interval);
            ZSetOperations<String, Ohlc> zSetOps = ohlcRedisTemplate.opsForZSet();
            Set<Ohlc> set = zSetOps.rangeByScore(redisKey, epochSeconds, epochSeconds);
            Ohlc ohlc = (set != null && !set.isEmpty()) ? set.iterator().next()
                    : new Ohlc(deal.lotPrice(),
                    deal.lotPrice(),
                    deal.lotPrice(),
                    deal.lotPrice(),
                    0L,
                    openTime
            );
            Ohlc updated = updateOhlc(ohlc, deal);
            zSetOps.remove(redisKey, ohlc);
            zSetOps.add(redisKey, updated, epochSeconds);
        }
    }

    public List<Ohlc> getOhlc(Long instrumentId, String interval, Instant from, Instant to) {
        Interval inter = Interval.fromString(interval).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Unsupported interval: %s", interval)));
        String redisKey = getOhlcRedisKey(instrumentId, inter);
        long fromEpoch = from.getEpochSecond();
        long toEpoch = to.getEpochSecond();
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
