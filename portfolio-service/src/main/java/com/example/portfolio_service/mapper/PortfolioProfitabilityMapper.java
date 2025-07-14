package com.example.portfolio_service.mapper;

import com.aws.protobuf.AnalyticServiceProto;
import com.example.portfolio_service.dto.profitability.PortfolioProfitabilityResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PortfolioProfitabilityMapper {
    PortfolioProfitabilityResponse mapToProfitabilityResponseDto(AnalyticServiceProto.PortfolioProfitabilityResponse response);
}
