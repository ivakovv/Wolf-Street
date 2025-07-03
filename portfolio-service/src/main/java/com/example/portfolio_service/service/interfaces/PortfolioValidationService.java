package com.example.portfolio_service.service.interfaces;

import java.math.BigDecimal;

public interface PortfolioValidationService {
    boolean validateAndBlockForSale(Long userId, Long portfolioId, Long instrumentId, Long count);
    boolean validateAndBlockForBuy(Long userId, Long portfolioId, String total);

    boolean unblockInstruments(Long userId, Long portfolioId, Long instrumentId, Long count);
    boolean unblockCash(Long userId, Long portfolioId, BigDecimal amount);
}
