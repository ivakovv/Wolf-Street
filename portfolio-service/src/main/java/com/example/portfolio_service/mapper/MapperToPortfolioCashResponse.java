package com.example.portfolio_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.portfolio_service.dto.PortfolioCashResponseDto;
import com.example.portfolio_service.entity.PortfolioCash;

@Mapper(componentModel = "spring")
public interface MapperToPortfolioCashResponse {
    @Mapping(target = "currency", source = "currency")
    @Mapping(target = "availableAmount", source = "availableAmount")
    @Mapping(target = "blockedAmount", source = "blockedAmount")
    @Mapping(target = "totalAmount", expression = "java(availableAmount.add(blockedAmount))")
    PortfolioCashResponseDto mapToPortfolioCashResponseDto(PortfolioCash portfolioCash);
}
