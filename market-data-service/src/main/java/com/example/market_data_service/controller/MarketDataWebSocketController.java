package com.example.market_data_service.controller;

import com.example.market_data_service.dto.ohlc.Ohlc;
import com.example.market_data_service.dto.orderbook.OrderBookAggregatedResponse;
import com.example.market_data_service.dto.orderbook.OrderBookResponse;
import com.example.market_data_service.dto.orderbook.SpreadResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class MarketDataWebSocketController {
    private final SimpMessagingTemplate messagingTemplate;

    public void sendOrderBookUpdate(Long instrumentId, OrderBookResponse orderBook) {
        messagingTemplate.convertAndSend("/topic/orderbook/" + instrumentId, orderBook);
    }

    public void sendAggregatedOrderBookUpdate(Long instrumentId, OrderBookAggregatedResponse orderBook){
        messagingTemplate.convertAndSend("/topic/aggregated/" + instrumentId, orderBook);
    }

    public void sendSpreadOrderBookUpdate(Long instrumentId, SpreadResponse spreadResponse){
        messagingTemplate.convertAndSend("/topic/spread/" + instrumentId, spreadResponse);
    }

    public void sendOhlcUpdate(Long instrumentId, String interval, Ohlc ohlc){
        messagingTemplate.convertAndSend("/topic/ohlc/" + instrumentId + "/" + interval, ohlc);
    }
}
