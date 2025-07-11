package com.example.market_data_service.service;

import com.example.market_data_service.dto.Deal;
import com.example.market_data_service.dto.Order;
import com.example.market_data_service.dto.enums.OrderType;
import com.example.market_data_service.service.interfaces.EventProcessor;
import com.example.market_data_service.service.interfaces.RedisOrderBookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventProcessorImpl implements EventProcessor {
    private final RedisOrderBookService redisOrderBookService;

    @Override
    public void processCreatedOrder(Order order) {
        log.info("Adding order: {} in order book", order.orderId());
        redisOrderBookService.addToOrderBook(order);
        redisOrderBookService.addOrderLevel(order.instrumentId(), order.type(), order.lotPrice().doubleValue(), order.count());
    }

    @Override
    public void processUpdatedOrder(Order order) {
        redisOrderBookService.removeFromOrderBook(order.orderId(), order.type(), order.instrumentId());
        if (order.count() != 0) redisOrderBookService.addToOrderBook(order);
    }

    @Override
    public void processCancelledOrder(Order order) {
        log.info("Removing order: {} from order book", order.orderId());
        redisOrderBookService.removeFromOrderBook(order.orderId(), order.type(), order.instrumentId());
        redisOrderBookService.removeOrderLevel(order.instrumentId(), order.type(), order.lotPrice().doubleValue(), order.count());
    }

    @Override
    public void processExecutedDeal(Deal deal) {
        log.info("Removing orders: {}, {} from order book", deal.buyOrderId(), deal.saleOrderId());
        redisOrderBookService.removeFromOrderBook(deal.buyOrderId(), OrderType.BUY, deal.instrumentId());
        redisOrderBookService.removeFromOrderBook(deal.saleOrderId(), OrderType.SALE, deal.instrumentId());
        redisOrderBookService.removeOrderLevel(deal.instrumentId(), OrderType.BUY, deal.buyOrderPrice().doubleValue(), deal.count());
        redisOrderBookService.removeOrderLevel(deal.instrumentId(), OrderType.SALE, deal.lotPrice().doubleValue(), deal.count());
    }
}
