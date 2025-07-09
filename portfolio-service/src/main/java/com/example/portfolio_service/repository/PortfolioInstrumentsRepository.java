package com.example.portfolio_service.repository;

import com.example.portfolio_service.entity.Portfolio;
import com.example.portfolio_service.entity.PortfolioInstruments;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PortfolioInstrumentsRepository extends JpaRepository<PortfolioInstruments, Long> {
    List<PortfolioInstruments> findAllByPortfolio(Portfolio portfolio);
    Optional<PortfolioInstruments> findByPortfolioAndInstrumentId(Portfolio portfolio, Long instrumentId);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<PortfolioInstruments> findForUpdateByPortfolioAndInstrumentId(Portfolio portfolio, Long instrumentId);
}
