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
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderBookServiceImpl implements OrderBookService {
    private final Long ORDERS_COUNT = 1000L;
    private final RedisOrderBookService redisOrderBookService;

    @Override
    public OrderBookResponse getOrderBook(Long instrumentId, Long limitOrders) {
        List<OrderBookEntry> bids = redisOrderBookService.getTopNOrders(OrderType.BUY, instrumentId, limitOrders);
        List<OrderBookEntry> asks = redisOrderBookService.getTopNOrders(OrderType.SALE, instrumentId, limitOrders);
        return new OrderBookResponse(instrumentId, bids, asks);
    }

    @Override
    public OrderBookAggregatedResponse getAggregatedOrderBook(Long instrumentId, Long limitLevels) {
        OrderBookResponse orderBook = getOrderBook(instrumentId, ORDERS_COUNT);
        Map<Double, Long> mappedBids = groupByPrice(orderBook.bids());
        Map<Double, Long> mappedAsks = groupByPrice(orderBook.asks());
        return new OrderBookAggregatedResponse(instrumentId,
                getAgregatedList(mappedBids, limitLevels, OrderType.BUY),
                getAgregatedList(mappedAsks, limitLevels, OrderType.SALE));
    }

    @Override
    public SpreadResponse getSpread(Long instrumentId) {
        BigDecimal bestBid = BigDecimal.valueOf(redisOrderBookService.getBestBid(instrumentId).price());
        BigDecimal bestAsk = BigDecimal.valueOf(redisOrderBookService.getBestAsk(instrumentId).price());
        BigDecimal spread = bestAsk.subtract(bestBid);
        BigDecimal midPrice = bestBid.add(bestAsk).divide(BigDecimal.valueOf(2), 10, RoundingMode.FLOOR);
        return new SpreadResponse(instrumentId, bestBid, bestAsk, spread, midPrice);
    }

    private Map<Double, Long> groupByPrice(List<OrderBookEntry> orderBookEntries) {
        return orderBookEntries.stream()
                .collect(Collectors.groupingBy(
                        OrderBookEntry::price,
                        Collectors.summingLong(OrderBookEntry::count)
                ));
    }
    private List<AggregatedOrderBookLevel> getAgregatedList(Map<Double, Long> mappedOrderBook, Long limitLevels, OrderType orderType){
        return mappedOrderBook.entrySet().stream()
                .map(entry -> new AggregatedOrderBookLevel(BigDecimal.valueOf(entry.getKey()), entry.getValue()))
                .sorted((a, b) ->{
                    switch (orderType){
                        case BUY -> {
                            return b.lotPrice().compareTo(a.lotPrice());
                        }
                        case SALE ->{
                            return a.lotPrice().compareTo(b.lotPrice());
                        }
                        default -> throw new IllegalArgumentException(String.format("Unknown order type: %s", orderType));
                    }
                })
                .limit(limitLevels)
                .toList();
    }
}
