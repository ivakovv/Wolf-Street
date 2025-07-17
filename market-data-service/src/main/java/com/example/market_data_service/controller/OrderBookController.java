package com.example.market_data_service.controller;

import com.example.market_data_service.dto.orderbook.OrderBookAggregatedResponse;
import com.example.market_data_service.dto.orderbook.OrderBookResponse;
import com.example.market_data_service.dto.orderbook.SpreadResponse;
import com.example.market_data_service.service.interfaces.OrderBookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/orderbook")
@RestController
@RequiredArgsConstructor
public class OrderBookController{
    private final OrderBookService orderBookService;

    @GetMapping("/{instrumentId}")
    @Operation(description = """
            WebSocket по адресу: /topic/orderbook/{instrumentId}
            """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Стакан успешно получен!"),
            @ApiResponse(responseCode = "404", description = "Стакан для данного инструмента не существует!"),
    })
    public ResponseEntity<OrderBookResponse> getOrderBook(@PathVariable Long instrumentId, @RequestParam Long limitOrders){
        return ResponseEntity.ok(orderBookService.getOrderBook(instrumentId, limitOrders));
    }
    @GetMapping("/{instrumentId}/aggregated")
    @Operation(description = """
            WebSocket по адресу: /topic/aggregated/{instrumentId}
            """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Агрегированный стакан получен!"),
            @ApiResponse(responseCode = "404", description = "Стакан для данного инструмента не существует!"),
    })
    public ResponseEntity<OrderBookAggregatedResponse> getAggregatedOrderBook(@PathVariable Long instrumentId, @RequestParam Long limitLevels){
        return ResponseEntity.ok(orderBookService.getAggregatedOrderBook(instrumentId, limitLevels));
    }
    @GetMapping("/{instrumentId}/spread")
    @Operation(description = """
            WebSocket по адресу: /topic/spread/{instrumentId}
            """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Spread для инструмента получен!"),
            @ApiResponse(responseCode = "404", description = "Стакан для данного инструмента не существует!"),
    })
    public ResponseEntity<SpreadResponse> getSpread(@PathVariable Long instrumentId){
        return ResponseEntity.ok(orderBookService.getSpread(instrumentId));
    }
}
