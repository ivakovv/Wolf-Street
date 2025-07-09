package com.example.portfolio_service.repository;

import com.example.portfolio_service.entity.Portfolio;
import com.example.portfolio_service.entity.PortfolioCash;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PortfolioCashRepository extends JpaRepository<PortfolioCash, Long> {
    List<PortfolioCash> findAllByPortfolio(Portfolio portfolio);
    Optional<PortfolioCash> findByPortfolioAndCurrency(Portfolio portfolio, String currency);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<PortfolioCash> findForUpdateByPortfolioAndCurrency(Portfolio portfolio, String currency);
}
