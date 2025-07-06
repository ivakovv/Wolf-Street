package com.example.portfolio_service.service.interfaces;

import com.aws.protobuf.UserMessages;
import com.example.portfolio_service.dto.InstrumentRequest;
import com.example.portfolio_service.dto.PortfolioCashResponseDto;
import com.example.portfolio_service.dto.PortfolioHistoryResponseDto;
import com.example.portfolio_service.dto.PortfolioInstrumentResponseDto;
import com.example.portfolio_service.dto.PortfolioValueResponseDto;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface PortfolioService {
    void createPortfoliosForNewUser(UserMessages.UserCreatedEvent userCreatedEvent);
    List<PortfolioInstrumentResponseDto> getInstrumentsForUser(Authentication authentication);
    List<PortfolioCashResponseDto> getCashForUser(Authentication authentication);
    void addInstrumentToPortfolio(Authentication authentication, InstrumentRequest request);
    void removeInstrumentFromPortfolio(Authentication authentication, InstrumentRequest request);
    PortfolioValueResponseDto getCurrentPortfolioValue(Authentication authentication);
    List<PortfolioHistoryResponseDto> getPortfolioHistory(Authentication authentication);
}
