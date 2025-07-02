package com.example.portfolio_service.service.interfaces;

import com.aws.protobuf.UserMessages;
import com.example.portfolio_service.dto.InstrumentRequest;
import com.example.portfolio_service.dto.PortfolioCashResponseDto;
import com.example.portfolio_service.dto.PortfolioInstrumentResponseDto;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface PortfolioService {
    void createPortfoliosForNewUser(UserMessages.UserCreatedEvent userCreatedEvent);
    List<PortfolioInstrumentResponseDto> getInstrumentsForUser(Authentication authentication);
    List<PortfolioCashResponseDto> getCashForUser(Authentication authentication);
    void addInstrumentToPortfolio(Authentication authentication, InstrumentRequest request);
    void removeInstrumentFromPortfolio(Authentication authentication, InstrumentRequest request);
}
