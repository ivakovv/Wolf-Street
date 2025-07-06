package com.example.portfolio_service.service;

import com.aws.protobuf.AnalyticServiceProto;
import com.aws.protobuf.UserMessages;
import com.example.portfolio_service.dto.InstrumentRequest;
import com.example.portfolio_service.dto.PortfolioCashResponseDto;
import com.example.portfolio_service.dto.PortfolioHistoryResponseDto;
import com.example.portfolio_service.dto.PortfolioInstrumentResponseDto;
import com.example.portfolio_service.dto.PortfolioValueResponseDto;
import com.example.portfolio_service.entity.Portfolio;
import com.example.portfolio_service.entity.PortfolioInstruments;
import com.example.portfolio_service.mapper.MapperToCashPortfolio;
import com.example.portfolio_service.mapper.MapperToPortfolioCashResponse;
import com.example.portfolio_service.mapper.MapperToPortfolioInstrument;
import com.example.portfolio_service.mapper.MapperToPortfolioInstrumentResponse;
import com.example.portfolio_service.mapper.MapperToPortfolioHistory;
import com.example.portfolio_service.repository.PortfolioCashRepository;
import com.example.portfolio_service.repository.PortfolioInstrumentsRepository;
import com.example.portfolio_service.repository.PortfolioRepository;
import com.example.portfolio_service.service.grpc.AnalyticServiceClient;
import com.example.portfolio_service.service.interfaces.PortfolioService;
import com.wolfstreet.security_lib.details.JwtDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PortfolioServiceImpl implements PortfolioService {
    private final PortfolioRepository portfolioRepository;
    private final PortfolioCashRepository portfolioCashRepository;
    private final PortfolioInstrumentsRepository portfolioInstrumentsRepository;
    private final AnalyticServiceClient analyticServiceClient;
    private final MapperToCashPortfolio mapperToCashPortfolio;
    private final MapperToPortfolioInstrument mapperToPortfolioInstrument;
    private final MapperToPortfolioInstrumentResponse mapperToPortfolioInstrumentResponse;
    private final MapperToPortfolioCashResponse mapperToPortfolioCashResponse;
    private final MapperToPortfolioHistory mapperToPortfolioHistory;

    @Override
    @Transactional
    public void createPortfoliosForNewUser(UserMessages.UserCreatedEvent userCreatedEvent) {
        Long userId = userCreatedEvent.getId();
        Portfolio savedPortfolio = createPortfolio(userId);
        log.info("Created portfolio with id: {}", userId);
        createCashPortfolio(savedPortfolio);
        log.info("Created cash for portfolio id: {}", savedPortfolio.getId());
        createInstrumentsPortfolio(savedPortfolio);
        log.info("Created instruments for portfolio id: {}", savedPortfolio.getId());
    }

    @Override
    public List<PortfolioInstrumentResponseDto> getInstrumentsForUser(Authentication authentication) {
        return portfolioInstrumentsRepository.findAllByPortfolio(getPortfolioFromAuth(authentication))
                .stream()
                .map(mapperToPortfolioInstrumentResponse::mapToPortfolioInstrumentResponseDto)
                .toList();
    }

    @Override
    public List<PortfolioCashResponseDto> getCashForUser(Authentication authentication) {
        return portfolioCashRepository.findAllByPortfolio(getPortfolioFromAuth(authentication))
                .stream()
                .map(mapperToPortfolioCashResponse::mapToPortfolioCashResponseDto)
                .toList();
    }

    @Override
    public void addInstrumentToPortfolio(Authentication authentication, InstrumentRequest request){
        Portfolio portfolio = getPortfolioFromAuth(authentication);
        PortfolioInstruments portfolioInstruments = mapperToPortfolioInstrument.mapToPortfolioInstrument(portfolio, request.instrumentId());
        portfolioInstrumentsRepository.save(portfolioInstruments);
    }

    @Override
    public void removeInstrumentFromPortfolio(Authentication authentication, InstrumentRequest request){
        Portfolio portfolio = getPortfolioFromAuth(authentication);
        PortfolioInstruments instrument = portfolioInstrumentsRepository
                .findByPortfolioAndInstrumentId(portfolio, request.instrumentId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Instrument %d not found in portfolio", request.instrumentId())));
        if (!isInstrumentEmpty(instrument)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Instrument %d contains values", request.instrumentId()));
        }
        portfolioInstrumentsRepository.delete(instrument);
    }

    //TODO скорее всего обращение к MarketData, для получения текущей информации по стоимости активов
    @Override
    public PortfolioValueResponseDto getCurrentPortfolioValue(Authentication authentication) {
        return null;
    }

    @Override
    public List<PortfolioHistoryResponseDto> getPortfolioHistory(Authentication authentication) {
        AnalyticServiceProto.PortfolioHistoryResponse portfolioHistory = analyticServiceClient.getPortfolioHistory(getPortfolioFromAuth(authentication).getId());
        return portfolioHistory.getDealsList().stream()
                .map(mapperToPortfolioHistory::mapToPortfolioHistoryDto)
                .toList();
    }

    private Portfolio createPortfolio(Long userId) {
        if (portfolioRepository.existsByUserId(userId)) {
            log.warn("Portfolio already exists for user id: {}", userId);
            throw new IllegalArgumentException();
        }
        Portfolio portfolio = Portfolio.builder()
                .userId(userId)
                .build();
        return portfolioRepository.save(portfolio);
    }

    private Portfolio getPortfolioFromAuth(Authentication authentication) {
        JwtDetails jwtDetails = (JwtDetails) authentication.getPrincipal();
        return portfolioRepository.findByUserId(jwtDetails.getUserId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Portfolio for user: %d doesn't exists", jwtDetails.getUserId())));
    }

    private void createCashPortfolio(Portfolio portfolio) {
        portfolioCashRepository.save(mapperToCashPortfolio.mapToPortfolioCash(portfolio));
    }
    private boolean isInstrumentEmpty(PortfolioInstruments instrument) {
        return instrument.getAvailableAmount().equals(0L) && instrument.getBlockedAmount().equals(0L);
    }


    //TODO сделать тут норм чтобы было
    private void createInstrumentsPortfolio(Portfolio portfolio) {
        for (long instrumentId = 1L; instrumentId <= 3L; instrumentId++) {
            portfolioInstrumentsRepository.save(mapperToPortfolioInstrument.mapToPortfolioInstrument(portfolio, instrumentId));
        }
    }

}
