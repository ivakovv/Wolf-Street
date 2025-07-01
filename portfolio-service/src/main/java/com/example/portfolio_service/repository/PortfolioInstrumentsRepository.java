package com.example.portfolio_service.repository;

import com.example.portfolio_service.entity.PortfolioInstruments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PortfolioInstrumentsRepository extends JpaRepository<PortfolioInstruments, Long> {
}
