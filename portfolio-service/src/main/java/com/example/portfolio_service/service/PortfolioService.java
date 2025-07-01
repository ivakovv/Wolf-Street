package com.example.portfolio_service.service;

import com.example.portfolio_service.repository.PortfolioCashRepository;
import com.example.portfolio_service.repository.PortfolioInstrumentsRepository;
import com.example.portfolio_service.repository.PortfolioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PortfolioService {
    private final PortfolioRepository portfolioRepository;
    private final PortfolioCashRepository portfolioCashRepository;
    private final PortfolioInstrumentsRepository portfolioInstrumentsRepository;
}
