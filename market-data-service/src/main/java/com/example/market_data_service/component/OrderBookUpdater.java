package com.example.market_data_service.component;

import com.example.market_data_service.controller.MarketDataWebSocketPublisher;
import com.example.market_data_service.dto.orderbook.OrderBookAggregatedResponse;
import com.example.market_data_service.dto.orderbook.OrderBookResponse;
import com.example.market_data_service.dto.orderbook.SpreadResponse;
import com.example.market_data_service.service.interfaces.OrderBookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderBookUpdater {
    private final OrderBookService orderBookService;
    private final MarketDataWebSocketPublisher marketDataWebSocketController;
    private final Set<Long> changedInstrumentIds = ConcurrentHashMap.newKeySet();
    private static final long ORDER_BOOK_LIMIT = 20L;

    public void markChangedInstrument(Long instrumentId) {
        changedInstrumentIds.add(instrumentId);
    }

    @Scheduled(fixedRate = 500)
    private void sendUpdates() {
        Set<Long> toUpdate = new HashSet<>(changedInstrumentIds);
        changedInstrumentIds.removeAll(toUpdate);
        for (Long instrumentId : toUpdate) {
            try {
                updateOrderBooksWs(instrumentId);
                log.info("Info successfully updated");
            } catch (Exception e) {
                log.error("Cann't update ws info: {}", e.getMessage());
            }
        }
    }

    private void updateOrderBooksWs(Long instrumentId) {
        OrderBookResponse orderBookResponse = orderBookService.getOrderBook(instrumentId, ORDER_BOOK_LIMIT);
        OrderBookAggregatedResponse orderBookAggregatedResponse = orderBookService.getAggregatedOrderBook(instrumentId, ORDER_BOOK_LIMIT);
        SpreadResponse spreadResponse = orderBookService.getSpread(instrumentId);
        marketDataWebSocketController.sendOrderBookUpdate(instrumentId, orderBookResponse);
        marketDataWebSocketController.sendAggregatedOrderBookUpdate(instrumentId, orderBookAggregatedResponse);
        marketDataWebSocketController.sendSpreadOrderBookUpdate(instrumentId, spreadResponse);
    }
}
