package com.example.portfolio_service.mapper;

import com.example.portfolio_service.entity.Portfolio;
import com.example.portfolio_service.entity.PortfolioInstruments;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MapperToPortfolioInstrument {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "portfolio", source = "portfolio")
    @Mapping(target = "instrumentId", source = "instrumentId")
    @Mapping(target = "availableAmount", expression = "java(0L)")
    @Mapping(target = "blockedAmount", expression = "java(0L)")
    @Mapping(target = "updatedAt", ignore = true)
    PortfolioInstruments mapToPortfolioInstrument(Portfolio portfolio, Long instrumentId);
}
