package com.example.portfolio_service.service;

import com.example.portfolio_service.entity.Portfolio;
import com.example.portfolio_service.entity.PortfolioCash;
import com.example.portfolio_service.entity.PortfolioInstruments;
import com.example.portfolio_service.repository.PortfolioCashRepository;
import com.example.portfolio_service.repository.PortfolioInstrumentsRepository;
import com.example.portfolio_service.repository.PortfolioRepository;
import com.example.portfolio_service.service.interfaces.PortfolioValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PortfolioValidationServiceImpl implements PortfolioValidationService {
    private final PortfolioRepository portfolioRepository;
    private final PortfolioInstrumentsRepository portfolioInstrumentsRepository;
    private final PortfolioCashRepository portfolioCashRepository;

    @Override
    public boolean validateAndBlockForSale(Long userId, Long portfolioId, Long instrumentId, Long count) {
        log.info("validating portfolio: {} for sale instrument: {} in count: {}", portfolioId, instrumentId, count);
        return findUserPortfolio(userId, portfolioId)
                .flatMap(portfolio -> findInstrument(portfolio, instrumentId))
                .map(instrument -> blockInstruments(instrument, count))
                .orElse(false);
    }

    @Override
    public boolean validateAndBlockForBuy(Long userId, Long portfolioId, String total) {
        log.info("validating portfolio: {} for buy on total: {}", portfolioId, total);
        return findUserPortfolio(userId, portfolioId)
                .flatMap(this::findCash)
                .map(cash -> blockCash(cash, new BigDecimal(total)))
                .orElse(false);
    }

    @Override
    @Transactional
    public void processExecutedDeal(
            Long buyUserId, Long saleUserId, Long portfolioBuyId, Long portfolioSaleId, Long instrumentId, Long count, BigDecimal lotPrice) {
        try {
            processDealForSaleSide(saleUserId, portfolioSaleId, instrumentId, count, lotPrice);
            processDealForBuySide(buyUserId, portfolioBuyId, instrumentId, count, lotPrice);
        } catch (Exception e) {
            log.error("process executed Deal failed: {}", e.getMessage());
        }
    }

    @Override
    @Transactional
    public void processCancelledDeal(
            Long buyUserId, Long saleUserId, Long portfolioBuyId, Long portfolioSaleId, Long instrumentId, Long count, BigDecimal lotPrice){
        try{
            unblockInstruments(saleUserId, portfolioSaleId, instrumentId, count);
            unblockCash(buyUserId, portfolioBuyId, lotPrice.multiply(new BigDecimal(count)));
        } catch (Exception e){
            log.error("process cancelled Deal failed: {}", e.getMessage());
        }
    }

    private void unblockInstruments(Long userId, Long portfolioId, Long instrumentId, Long count) {
        PortfolioInstruments instrument = findUserPortfolio(userId, portfolioId)
                .flatMap(portfolio -> findInstrument(portfolio, instrumentId))
                .orElseThrow(() -> new RuntimeException(
                        String.format("Instrument %d for user %d doesn't exists", instrumentId, userId)
                ));
        if (!releaseInstruments(instrument, count)) {
            throw new RuntimeException(
                    String.format("The blocked count less than provided count %d", count)
            );
        }
    }

    private void unblockCash(Long userId, Long portfolioId, BigDecimal amount) {
        PortfolioCash cash = findUserPortfolio(userId, portfolioId)
                .flatMap(this::findCash)
                .orElseThrow(() -> new RuntimeException(
                        String.format("Cash for user: %d doesn't exists", userId)
                ));
        if(!releaseCash(cash, amount)){
            throw new RuntimeException(
                    String.format("The blocked amount less than provided amount %s", amount)
            );
        }
    }

    private void processDealForSaleSide(Long saleUserId, Long portfolioSaleId, Long instrumentId, Long count, BigDecimal lotPrice) {
        Portfolio portfolio = getUserPortfolio(saleUserId, portfolioSaleId);
        updateAvailableCash(portfolio, lotPrice.multiply(new BigDecimal(count)));
        updateBlockedInstrument(portfolio, instrumentId, -count);
    }

    private void processDealForBuySide(Long buyUserId, Long portfolioBuyId, Long instrumentId, Long count, BigDecimal lotPrice) {
        Portfolio portfolio = getUserPortfolio(buyUserId, portfolioBuyId);
        updateBlockedCash(portfolio, lotPrice.multiply(new BigDecimal(count)).negate());
        updateAvailableInstrument(portfolio, instrumentId, count);
    }

    private Optional<Portfolio> findUserPortfolio(Long userId, Long portfolioId) {
        return portfolioRepository.findById(portfolioId)
                .filter(portfolio -> portfolio.getUserId().equals(userId));
    }

    private Optional<PortfolioInstruments> findInstrument(Portfolio portfolio, Long instrumentId) {
        return portfolioInstrumentsRepository.findByPortfolioAndInstrumentId(portfolio, instrumentId);
    }

    private Optional<PortfolioCash> findCash(Portfolio portfolio) {
        return portfolioCashRepository.findByPortfolioAndCurrency(portfolio, "RUB");
    }

    private Portfolio getUserPortfolio(Long userId, Long portfolioId) {
        return findUserPortfolio(userId, portfolioId).orElseThrow(
                () -> new IllegalArgumentException(String.format("Portfolio %d for user %d doesn't exists", portfolioId, userId)));
    }

    private PortfolioInstruments getInstrument(Portfolio portfolio, Long instrumentId) {
        return findInstrument(portfolio, instrumentId).orElseThrow(
                () -> new IllegalArgumentException(String.format("Instrument: %d in portfolio: %d doesn't exists", instrumentId, portfolio.getId())));
    }

    private PortfolioCash getCash(Portfolio portfolio) {
        return findCash(portfolio).orElseThrow(
                () -> new IllegalArgumentException(String.format("Cash in portfolio %d doesn't exists", portfolio.getId())));
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

    private void updateBlockedInstrument(Portfolio portfolio, Long instrumentId, Long countChange) {
        PortfolioInstruments portfolioInstruments = getInstrument(portfolio, instrumentId);
        portfolioInstruments.setBlockedAmount(portfolioInstruments.getBlockedAmount() + countChange);
        portfolioInstrumentsRepository.save(portfolioInstruments);
    }

    private void updateBlockedCash(Portfolio portfolio, BigDecimal amountChange) {
        PortfolioCash portfolioCash = getCash(portfolio);
        portfolioCash.setBlockedAmount(portfolioCash.getBlockedAmount().add(amountChange));
        portfolioCashRepository.save(portfolioCash);
    }

    private void updateAvailableInstrument(Portfolio portfolio, Long instrumentId, Long countChange) {
        PortfolioInstruments portfolioInstruments = findInstrument(portfolio, instrumentId)
                .orElse(PortfolioInstruments.builder()
                        .portfolio(portfolio)
                        .instrumentId(instrumentId)
                        .availableAmount(0L)
                        .blockedAmount(0L)
                        .build());
        portfolioInstruments.setAvailableAmount(portfolioInstruments.getAvailableAmount() + countChange);
        portfolioInstrumentsRepository.save(portfolioInstruments);
    }

    private void updateAvailableCash(Portfolio portfolio, BigDecimal amountChange) {
        PortfolioCash portfolioCash = getCash(portfolio);
        portfolioCash.setAvailableAmount(portfolioCash.getAvailableAmount().add(amountChange));
        portfolioCashRepository.save(portfolioCash);
    }

}


