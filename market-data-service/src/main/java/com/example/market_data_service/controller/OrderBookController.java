package com.example.market_data_service.controller;

import com.example.market_data_service.dto.orderbook.OrderBookAggregatedResponse;
import com.example.market_data_service.dto.orderbook.OrderBookResponse;
import com.example.market_data_service.service.interfaces.OrderBookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/orderbook")
@RestController
@RequiredArgsConstructor
public class OrderBookController{
    private final OrderBookService orderBookService;

    @GetMapping("/{instrumentId}")
    public ResponseEntity<OrderBookResponse> getOrderBook(@PathVariable Long instrumentId, @RequestParam Long limitOrders){
        return ResponseEntity.ok(orderBookService.getOrderBook(instrumentId, limitOrders));
    }
    @GetMapping("/{instrumentId}/aggregated")
    public ResponseEntity<OrderBookAggregatedResponse> getAggregatedOrderBook(@PathVariable Long instrumentId, @RequestParam Long limitLevels){
        return ResponseEntity.ok(orderBookService.getAggregatedOrderBook(instrumentId, limitLevels));
    }
}
