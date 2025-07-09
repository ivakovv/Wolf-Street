package com.example.portfolio_service.service.interfaces;

import com.example.portfolio_service.enums.OrderType;

import java.math.BigDecimal;

public interface PortfolioValidationService {
    boolean validateAndBlockForSale(Long userId, Long portfolioId, Long instrumentId, Long count);
    boolean validateAndBlockForBuy(Long userId, Long portfolioId, String total);
    void processExecutedDeal(Long portfolioBuyId, Long portfolioSaleId, Long instrumentId, Long count, BigDecimal lotPrice, BigDecimal buyOrderPrice);
    void processCancelledDeal(Long portfolioId, Long instrumentId, Long count, BigDecimal lotPrice, OrderType orderType);
}
