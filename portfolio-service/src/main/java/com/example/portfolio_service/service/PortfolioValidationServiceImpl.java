package com.example.portfolio_service.service;

import com.example.portfolio_service.entity.Portfolio;
import com.example.portfolio_service.entity.PortfolioCash;
import com.example.portfolio_service.entity.PortfolioInstruments;
import com.example.portfolio_service.repository.PortfolioCashRepository;
import com.example.portfolio_service.repository.PortfolioInstrumentsRepository;
import com.example.portfolio_service.repository.PortfolioRepository;
import com.example.portfolio_service.service.interfaces.PortfolioValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PortfolioValidationServiceImpl implements PortfolioValidationService {
    private final PortfolioRepository portfolioRepository;
    private final PortfolioInstrumentsRepository portfolioInstrumentsRepository;
    private final PortfolioCashRepository portfolioCashRepository;

    @Override
    public boolean validateAndBlockForSale(Long userId, Long portfolioId, Long instrumentId, Long count) {
        return getUserPortfolio(userId, portfolioId)
                .flatMap(portfolio -> findInstrument(portfolio, instrumentId))
                .map(instrument -> blockInstruments(instrument, count))
                .orElse(false);
    }

    @Override
    public boolean validateAndBlockForBuy(Long userId, Long portfolioId, String total) {
        return getUserPortfolio(userId, portfolioId)
                .flatMap(this::findCash)
                .map(cash -> blockCash(cash, new BigDecimal(total)))
                .orElse(false);
    }

    @Override
    @Transactional
    public boolean unblockInstruments(Long userId, Long portfolioId, Long instrumentId, Long count) {
        return getUserPortfolio(userId, portfolioId)
                .flatMap(portfolio -> findInstrument(portfolio, instrumentId))
                .map(instrument -> releaseInstruments(instrument, count))
                .orElse(false);
    }

    @Override
    @Transactional
    public boolean unblockCash(Long userId, Long portfolioId, BigDecimal amount) {
        return getUserPortfolio(userId, portfolioId)
                .flatMap(this::findCash)
                .map(cash -> releaseCash(cash, amount))
                .orElse(false);
    }

    private Optional<Portfolio> getUserPortfolio(Long userId, Long portfolioId) {
        return portfolioRepository.findById(portfolioId)
                .filter(portfolio -> portfolio.getUserId().equals(userId));
    }

    private Optional<PortfolioInstruments> findInstrument(Portfolio portfolio, Long instrumentId) {
        return portfolioInstrumentsRepository.findByPortfolioAndInstrumentId(portfolio, instrumentId);
    }

    private Optional<PortfolioCash> findCash(Portfolio portfolio) {
        return portfolioCashRepository.findByPortfolioAndCurrency(portfolio, "RUB");
    }

    private boolean blockInstruments(PortfolioInstruments instrument, Long count) {
        if (instrument.getAvailableAmount() < count) {
            return false;
        }
        instrument.setAvailableAmount(instrument.getAvailableAmount() - count);
        instrument.setBlockedAmount(instrument.getBlockedAmount() + count);
        portfolioInstrumentsRepository.save(instrument);
        return true;
    }

    private boolean blockCash(PortfolioCash cash, BigDecimal amount) {
        if (cash.getAvailableAmount().compareTo(amount) < 0) {
            return false;
        }
        cash.setAvailableAmount(cash.getAvailableAmount().subtract(amount));
        cash.setBlockedAmount(cash.getBlockedAmount().add(amount));
        portfolioCashRepository.save(cash);
        return true;
    }

    private boolean releaseInstruments(PortfolioInstruments instrument, Long count) {
        if (instrument.getBlockedAmount() < count) {
            return false;
        }
        instrument.setAvailableAmount(instrument.getAvailableAmount() + count);
        instrument.setBlockedAmount(instrument.getBlockedAmount() - count);
        portfolioInstrumentsRepository.save(instrument);
        return true;
    }

    private boolean releaseCash(PortfolioCash cash, BigDecimal amount) {
        if (cash.getBlockedAmount().compareTo(amount) < 0) {
            return false;
        }
        cash.setAvailableAmount(cash.getAvailableAmount().add(amount));
        cash.setBlockedAmount(cash.getBlockedAmount().subtract(amount));
        portfolioCashRepository.save(cash);
        return true;
    }
}


