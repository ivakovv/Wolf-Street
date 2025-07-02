package com.example.portfolio_service.service;

import com.example.portfolio_service.entity.Portfolio;
import com.example.portfolio_service.repository.PortfolioCashRepository;
import com.example.portfolio_service.repository.PortfolioInstrumentsRepository;
import com.example.portfolio_service.repository.PortfolioRepository;
import com.example.portfolio_service.service.interfaces.PortfolioValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PortfolioValidationServiceImpl implements PortfolioValidationService {
    private final PortfolioRepository portfolioRepository;
    private final PortfolioInstrumentsRepository portfolioInstrumentsRepository;
    private final PortfolioCashRepository portfolioCashRepository;

    @Override
    public boolean isValidForSale(Long userId, Long portfolioId, Long instrumentId, Long count) {
        return getUserPortfolio(userId, portfolioId)
                .map(portfolio -> hasInstrumentForSale(portfolio, instrumentId, count))
                .orElse(false);
    }

    @Override
    public boolean isValidForBuy(Long userId, Long portfolioId, BigDecimal total) {
        return getUserPortfolio(userId, portfolioId)
                .map(portfolio -> hasCashForBuy(portfolio, total))
                .orElse(false);
    }

    private Optional<Portfolio> getUserPortfolio(Long userId, Long portfolioId) {
        return portfolioRepository.findById(portfolioId)
                .filter(portfolio -> portfolio.getUserId().equals(userId));
    }

    private boolean hasInstrumentForSale(Portfolio portfolio, Long instrumentId, Long count) {
        return portfolioInstrumentsRepository
                .findByPortfolioAndInstrumentId(portfolio, instrumentId)
                .map(instrument -> instrument.getAvailableAmount() >= count)
                .orElse(false);
    }

    private boolean hasCashForBuy(Portfolio portfolio, BigDecimal total){
        return portfolioCashRepository
                .findByPortfolioAndCurrency(portfolio, "RUB")
                .map(cash -> cash.getAvailableAmount().compareTo(total) >= 0)
                .orElse(false);
    }

}


