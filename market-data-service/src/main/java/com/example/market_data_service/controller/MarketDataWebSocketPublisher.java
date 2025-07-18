package com.example.market_data_service.controller;

import com.example.market_data_service.dto.DealResponse;
import com.example.market_data_service.dto.ohlc.Ohlc;
import com.example.market_data_service.dto.orderbook.OrderBookAggregatedResponse;
import com.example.market_data_service.dto.orderbook.OrderBookResponse;
import com.example.market_data_service.dto.orderbook.SpreadResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class MarketDataWebSocketPublisher {
    private final SimpMessagingTemplate messagingTemplate;
    private static final String ORDERBOOK_TOPIC = "/topic/orderbook/%d";
    private static final String AGGREGATED_ORDERBOOK_TOPIC = "/topic/aggregated/%d";
    private static final String SPREAD_TOPIC = "/topic/spread/%d";
    private static final String OHLC_TOPIC = "/topic/ohlc/%d/%s";
    private static final String DEALS_TOPIC = "/topic/deals/%d";

    public void sendOrderBookUpdate(Long instrumentId, OrderBookResponse orderBook) {
        messagingTemplate.convertAndSend(String.format(ORDERBOOK_TOPIC, instrumentId), orderBook);
    }

    public void sendAggregatedOrderBookUpdate(Long instrumentId, OrderBookAggregatedResponse orderBook) {
        messagingTemplate.convertAndSend(String.format(AGGREGATED_ORDERBOOK_TOPIC, instrumentId), orderBook);
    }

    public void sendSpreadOrderBookUpdate(Long instrumentId, SpreadResponse spreadResponse) {
        messagingTemplate.convertAndSend(String.format(SPREAD_TOPIC, instrumentId), spreadResponse);
    }

    public void sendOhlcUpdate(Long instrumentId, String interval, Ohlc ohlc) {
        messagingTemplate.convertAndSend(String.format(OHLC_TOPIC, instrumentId, interval), ohlc);
    }

    public void sendDealExecuted(Long instrumentId, DealResponse deal) {
        messagingTemplate.convertAndSend(String.format(DEALS_TOPIC, instrumentId), deal);
    }
}
