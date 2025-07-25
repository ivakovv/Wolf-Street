package com.example.portfolio_service.service.interfaces;

import com.aws.protobuf.UserMessages;
import com.example.portfolio_service.dto.CashRequestDto;
import com.example.portfolio_service.dto.InstrumentRequest;
import com.example.portfolio_service.dto.PortfolioCashResponseDto;
import com.example.portfolio_service.dto.PortfolioHistoryResponseDto;
import com.example.portfolio_service.dto.PortfolioInstrumentResponseDto;
import com.example.portfolio_service.dto.PortfolioResponseDto;
import com.example.portfolio_service.dto.PortfolioValueResponseDto;
import com.example.portfolio_service.dto.profitability.PortfolioProfitabilityRequest;
import com.example.portfolio_service.dto.profitability.PortfolioProfitabilityResponse;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface PortfolioService {
    PortfolioResponseDto getPortfolioForUser(Authentication authentication);
    void createPortfoliosForNewUser(UserMessages.UserCreatedEvent userCreatedEvent);
    List<PortfolioInstrumentResponseDto> getInstrumentsForUser(Authentication authentication);
    List<PortfolioCashResponseDto> getCashForUser(Authentication authentication);
    void addInstrumentToPortfolio(Authentication authentication, InstrumentRequest request);
    void removeInstrumentFromPortfolio(Authentication authentication, InstrumentRequest request);
    List<PortfolioValueResponseDto> getCurrentPortfolioValue(Authentication authentication);
    List<PortfolioHistoryResponseDto> getPortfolioHistory(Authentication authentication, Long from, Long to);
    PortfolioCashResponseDto addCash(Authentication authentication, CashRequestDto request);
    PortfolioProfitabilityResponse getProfitability(PortfolioProfitabilityRequest request, Authentication authentication);
}
