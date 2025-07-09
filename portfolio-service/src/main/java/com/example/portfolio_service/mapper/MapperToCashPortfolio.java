package com.example.portfolio_service.mapper;

import java.math.BigDecimal;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.portfolio_service.entity.Portfolio;
import com.example.portfolio_service.entity.PortfolioCash;

@Mapper(componentModel = "spring", imports = {BigDecimal.class})
public interface MapperToCashPortfolio {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "portfolio", source = "portfolio")
    @Mapping(target = "currency", constant = "RUB")
    @Mapping(target = "availableAmount", expression = "java(BigDecimal.ZERO)")
    @Mapping(target = "blockedAmount", expression = "java(BigDecimal.ZERO)")
    @Mapping(target = "updatedAt", ignore = true)
    PortfolioCash mapToPortfolioCash(Portfolio portfolio);
}