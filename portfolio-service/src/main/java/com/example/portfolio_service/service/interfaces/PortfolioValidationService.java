package com.example.portfolio_service.service.interfaces;

import java.math.BigDecimal;

public interface PortfolioValidationService {
    boolean isValidForSale(Long userId, Long portfolioId, Long instrumentId, Long count);
    boolean isValidForBuy(Long userId, Long portfolioId, BigDecimal total);
}
