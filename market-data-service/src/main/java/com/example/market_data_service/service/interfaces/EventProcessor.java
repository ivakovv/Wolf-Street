package com.example.market_data_service.service.interfaces;

import com.example.market_data_service.dto.Deal;
import com.example.market_data_service.dto.Order;

public interface EventProcessor {
    void processCreatedOrder(Order order);
    void processCancelledOrder(Order order);
    void processExecutedDeal(Deal deal);
    void processCancelledDeal(Long dealId);
}
