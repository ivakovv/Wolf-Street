package com.example.portfolio_service.service.interfaces;

import java.math.BigDecimal;

public interface PortfolioValidationService {
    boolean validateAndBlockForSale(Long userId, Long portfolioId, Long instrumentId, Long count);
    boolean validateAndBlockForBuy(Long userId, Long portfolioId, String total);
    void processExecutedDeal(Long buyUserId, Long saleUserId, Long portfolioBuyId, Long portfolioSaleId, Long instrumentId, Long count, BigDecimal lotPrice);
    void processCancelledDeal(Long buyUserId, Long saleUserId, Long portfolioBuyId, Long portfolioSaleId, Long instrumentId, Long count, BigDecimal lotPrice);
}
