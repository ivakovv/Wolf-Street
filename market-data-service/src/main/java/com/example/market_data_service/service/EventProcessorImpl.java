package com.example.market_data_service.service;

import com.example.market_data_service.controller.MarketDataWebSocketController;
import com.example.market_data_service.dto.Deal;
import com.example.market_data_service.dto.Order;
import com.example.market_data_service.dto.enums.OrderType;
import com.example.market_data_service.dto.orderbook.OrderBookAggregatedResponse;
import com.example.market_data_service.dto.orderbook.OrderBookResponse;
import com.example.market_data_service.dto.orderbook.SpreadResponse;
import com.example.market_data_service.service.interfaces.EventProcessor;
import com.example.market_data_service.service.interfaces.OrderBookService;
import com.example.market_data_service.service.interfaces.RedisOhlcService;
import com.example.market_data_service.service.interfaces.RedisOrderBookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventProcessorImpl implements EventProcessor {
    private final RedisOrderBookService redisOrderBookService;
    private final RedisOhlcService redisOhlcService;
    private final OrderBookService orderBookService;
    private final MarketDataWebSocketController marketDataWebSocketController;

    @Override
    public void processCreatedOrder(Order order) {
        log.info("Adding order: {} in order book", order.orderId());
        redisOrderBookService.addToOrderBook(order);
        redisOrderBookService.addOrderLevel(order.instrumentId(), order.type(), order.lotPrice().doubleValue(), order.count());
        updateOrderBooksWs(order.instrumentId());
    }

    @Override
    public void processUpdatedOrder(Order order) {
        redisOrderBookService.removeFromOrderBook(order.orderId(), order.type(), order.instrumentId());
        if (order.count() != 0) redisOrderBookService.addToOrderBook(order);
        updateOrderBooksWs(order.instrumentId());
    }

    @Override
    public void processCancelledOrder(Order order) {
        log.info("Removing order: {} from order book", order.orderId());
        redisOrderBookService.removeFromOrderBook(order.orderId(), order.type(), order.instrumentId());
        redisOrderBookService.removeOrderLevel(order.instrumentId(), order.type(), order.lotPrice().doubleValue(), order.count());
        updateOrderBooksWs(order.instrumentId());
    }

    @Override
    public void processExecutedDeal(Deal deal) {
        log.info("Removing orders: {}, {} from order book", deal.buyOrderId(), deal.saleOrderId());
        redisOrderBookService.removeFromOrderBook(deal.buyOrderId(), OrderType.BUY, deal.instrumentId());
        redisOrderBookService.removeFromOrderBook(deal.saleOrderId(), OrderType.SALE, deal.instrumentId());
        redisOrderBookService.removeOrderLevel(deal.instrumentId(), OrderType.BUY, deal.buyOrderPrice().doubleValue(), deal.count());
        redisOrderBookService.removeOrderLevel(deal.instrumentId(), OrderType.SALE, deal.lotPrice().doubleValue(), deal.count());
        log.info("Updating ohlc...");
        redisOhlcService.processDeal(deal);
        updateOrderBooksWs(deal.instrumentId());
    }

    private void updateOrderBooksWs(Long instrumentId){
        OrderBookResponse orderBookResponse = orderBookService.getOrderBook(instrumentId, 20L);
        OrderBookAggregatedResponse orderBookAggregatedResponse = orderBookService.getAggregatedOrderBook(instrumentId, 20L);
        SpreadResponse spreadResponse = orderBookService.getSpread(instrumentId);
        marketDataWebSocketController.sendOrderBookUpdate(instrumentId, orderBookResponse);
        marketDataWebSocketController.sendAggregatedOrderBookUpdate(instrumentId, orderBookAggregatedResponse);
        marketDataWebSocketController.sendSpreadOrderBookUpdate(instrumentId, spreadResponse);
    }
}
