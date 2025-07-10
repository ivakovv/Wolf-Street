package com.example.market_data_service.dto.orderbook;

public record OrderBookEntry(Long orderId, Long count, double price, Long portfolioId){
}
