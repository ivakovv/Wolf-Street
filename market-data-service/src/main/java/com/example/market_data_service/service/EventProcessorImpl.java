package com.example.market_data_service.service;

import com.example.market_data_service.dto.Deal;
import com.example.market_data_service.dto.Order;
import com.example.market_data_service.service.interfaces.EventProcessor;
import com.example.market_data_service.service.interfaces.RedisOrderBookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventProcessorImpl  implements EventProcessor {
    private final RedisOrderBookService redisOrderBookService;
    @Override
    public void processCreatedOrder(Order order) {
        log.info("Adding order: {} in order book", order.orderId());
        redisOrderBookService.addToOrderBook(order);
    }

    @Override
    public void processCancelledOrder(Long orderId) {

    }

    @Override
    public void processExecutedDeal(Deal deal) {

    }

    @Override
    public void processCancelledDeal(Long dealId) {

    }
}
