package com.example.market_data_service.service.interfaces;

import com.example.market_data_service.dto.Deal;
import com.example.market_data_service.dto.enums.Interval;
import com.example.market_data_service.dto.ohlc.Ohlc;

import java.time.OffsetDateTime;
import java.util.List;

public interface RedisOhlcService {
    void processDeal(Deal deal);
    List<Ohlc> getOhlc(Long instrumentId, Interval interval, OffsetDateTime from, OffsetDateTime to);
}
