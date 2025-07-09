package com.example.portfolio_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.portfolio_service.dto.PortfolioInstrumentResponseDto;
import com.example.portfolio_service.entity.PortfolioInstruments;

@Mapper(componentModel = "spring")
public interface MapperToPortfolioInstrumentResponse {
    @Mapping(target = "instrumentId", source = "instrumentId")
    @Mapping(target = "availableAmount", source = "availableAmount")
    @Mapping(target = "blockedAmount", source = "blockedAmount")
    @Mapping(target = "totalAmount", expression = "java(availableAmount+blockedAmount)")
    PortfolioInstrumentResponseDto mapToPortfolioInstrumentResponseDto(PortfolioInstruments portfolioInstruments);
}
