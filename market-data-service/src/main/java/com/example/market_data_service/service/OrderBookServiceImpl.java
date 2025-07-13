package com.example.market_data_service.service;

import com.example.market_data_service.dto.enums.OrderType;
import com.example.market_data_service.dto.orderbook.AggregatedOrderBookLevel;
import com.example.market_data_service.dto.orderbook.OrderBookAggregatedResponse;
import com.example.market_data_service.dto.orderbook.OrderBookEntry;
import com.example.market_data_service.dto.orderbook.OrderBookResponse;
import com.example.market_data_service.dto.orderbook.SpreadResponse;
import com.example.market_data_service.service.interfaces.OrderBookService;
import com.example.market_data_service.service.interfaces.RedisOrderBookService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderBookServiceImpl implements OrderBookService {
    private final RedisOrderBookService redisOrderBookService;

    @Override
    public OrderBookResponse getOrderBook(Long instrumentId, Long limitOrders) {
        List<OrderBookEntry> bids = redisOrderBookService.getTopNOrders(OrderType.BUY, instrumentId, limitOrders);
        List<OrderBookEntry> asks = redisOrderBookService.getTopNOrders(OrderType.SALE, instrumentId, limitOrders);
        return new OrderBookResponse(instrumentId, bids, asks);
    }

    @Override
    public OrderBookAggregatedResponse getAggregatedOrderBook(Long instrumentId, Long limitLevels) {
        List<AggregatedOrderBookLevel> bids = redisOrderBookService.getAggregatedLevels(instrumentId, OrderType.BUY, limitLevels);
        List<AggregatedOrderBookLevel> asks = redisOrderBookService.getAggregatedLevels(instrumentId, OrderType.SALE, limitLevels);
        return new OrderBookAggregatedResponse(instrumentId, bids, asks);
    }

    @Override
    public SpreadResponse getSpread(Long instrumentId) {
        OrderBookEntry bestBidEntry = redisOrderBookService.getBestBid(instrumentId);
        OrderBookEntry bestAskEntry = redisOrderBookService.getBestAsk(instrumentId);
        if (bestBidEntry == null || bestAskEntry == null) {
            return new SpreadResponse(instrumentId, null, null, null, null);
        }
        BigDecimal bestBid = BigDecimal.valueOf(bestBidEntry.price());
        BigDecimal bestAsk = BigDecimal.valueOf(bestAskEntry.price());
        BigDecimal spread = bestAsk.subtract(bestBid);
        BigDecimal midPrice = bestBid.add(bestAsk).divide(BigDecimal.valueOf(2), 10, RoundingMode.FLOOR);
        return new SpreadResponse(instrumentId, bestBid, bestAsk, spread, midPrice);
    }
}
