package com.example.market_data_service.mapper;

import com.aws.protobuf.DealMessages;
import com.example.market_data_service.dto.Deal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Mapper(componentModel = "spring", imports = {BigDecimal.class})
public interface MapperFromEventToDeal {
    @Mapping(target = "buyOrderId", source = "buyOrderId")
    @Mapping(target = "saleOrderId", source = "saleOrderId")
    @Mapping(target = "buyPortfolioId", source = "buyPortfolioId")
    @Mapping(target = "salePortfolioId", source = "salePortfolioId")
    @Mapping(target = "instrumentId", source = "instrumentId")
    @Mapping(target = "count", source = "count")
    @Mapping(target = "lotPrice", expression = "java(new BigDecimal(dealExecutedEvent.getLotPrice()))")
    @Mapping(target = "saleOrderPrice", expression = "java(new BigDecimal(dealExecutedEvent.getSaleOrderPrice()))")
    @Mapping(target = "buyOrderPrice", expression = "java(new BigDecimal(dealExecutedEvent.getBuyOrderPrice()))")
    @Mapping(target = "createdAt", expression = "java(toOffsetDateTime(dealExecutedEvent.getCreatedAt()))")
    Deal mapToDealFromEvent(DealMessages.DealExecutedEvent dealExecutedEvent);
    default OffsetDateTime toOffsetDateTime(com.google.protobuf.Timestamp timestamp) {
        if (timestamp == null) {
            return null;
        }
        return Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos())
                .atOffset(ZoneOffset.UTC);
    }
}
