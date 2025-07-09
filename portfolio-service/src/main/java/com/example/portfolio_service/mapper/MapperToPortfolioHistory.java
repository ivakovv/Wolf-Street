package com.example.portfolio_service.mapper;

import com.aws.protobuf.AnalyticServiceProto;
import com.example.portfolio_service.dto.PortfolioHistoryResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Mapper(componentModel = "spring", imports = {BigDecimal.class})
public interface MapperToPortfolioHistory {
    @Mapping(target = "dealType", expression = "java(deal.getDealType().toString())")
    @Mapping(target = "instrumentId", source = "instrumentId")
    @Mapping(target = "count", source = "count")
    @Mapping(target = "lotPrice", expression = "java(new BigDecimal(deal.getLotPrice()))")
    @Mapping(target = "totalAmount", expression = "java(new BigDecimal(deal.getLotPrice()).multiply(BigDecimal.valueOf(deal.getCount())))")
    @Mapping(target = "completedAt", expression = "java(toOffsetDateTime(deal.getTimestamp()))")
    PortfolioHistoryResponseDto mapToPortfolioHistoryDto(AnalyticServiceProto.Deal deal);
    default OffsetDateTime toOffsetDateTime(com.google.protobuf.Timestamp timestamp) {
        if (timestamp == null) {
            return null;
        }
        return Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos())
                .atOffset(ZoneOffset.UTC);
    }
}
