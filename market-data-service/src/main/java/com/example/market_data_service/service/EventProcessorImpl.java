package com.example.market_data_service.service;

import com.example.market_data_service.dto.Deal;
import com.example.market_data_service.dto.Order;
import com.example.market_data_service.dto.enums.OrderType;
import com.example.market_data_service.dto.orderbook.OrderBookEntry;
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
    }

    @Override
    public void processCancelledOrder(Order order) {
        log.info("Removing order: {} from order book", order.orderId());
        redisOrderBookService.removeFromOrderBook(getOrderBookEntryFromOrder(order), order.type(), order.instrumentId());
    }

    @Override
    public void processExecutedDeal(Deal deal) {
        log.info("Removing orders: {}, {} from order book", deal.buyOrderId(), deal.saleOrderId());
        redisOrderBookService.removeFromOrderBook(getOrderBookEntryFromDeal(deal, OrderType.BUY), OrderType.BUY, deal.instrumentId());
        redisOrderBookService.removeFromOrderBook(getOrderBookEntryFromDeal(deal, OrderType.SALE), OrderType.SALE, deal.instrumentId());
    }

    @Override
    public void processCancelledDeal(Long dealId) {

    }

    private OrderBookEntry getOrderBookEntryFromDeal(Deal deal, OrderType orderType) {
        switch (orderType) {
            case BUY -> {
                return new OrderBookEntry(deal.buyOrderId(), deal.count(), deal.lotPrice().doubleValue(), deal.buyPortfolioId());
            }
            case SALE -> {
                return new OrderBookEntry(deal.saleOrderId(), deal.count(), deal.lotPrice().doubleValue(), deal.salePortfolioId());
            }
            default -> throw new IllegalArgumentException(String.format("Unknown order type: %s", orderType));
        }
    }
    private OrderBookEntry getOrderBookEntryFromOrder(Order order){
        return new OrderBookEntry(order.orderId(), order.count(), order.lotPrice().doubleValue(), order.portfolioId());
    }
}
