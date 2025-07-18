package com.example.analytic_service.mapper;

import com.aws.protobuf.DealMessages;
import com.example.analytic_service.entity.ExecutedDeal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Mapper(componentModel = "spring")
public interface MapperToExecutedDeal {

    @Mapping(expression = "java(event.getBuyOrderId())", target = "orderId")
    @Mapping(expression = "java(event.getBuyPortfolioId())", target = "portfolioId")
    @Mapping(expression = "java(event.getCount())", target = "count")
    @Mapping(expression = "java(stringToBigDecimal(event.getLotPrice()))", target = "lotPrice")
    @Mapping(expression = "java(event.getInstrumentId())", target = "instrumentId")
    @Mapping(expression = "java(com.example.analytic_service.enums.OrderType.BUY)", target = "orderType")
    @Mapping(expression = "java(timestampToOffsetDateTime(event.getCreatedAt()))", target = "createdAt")
    ExecutedDeal mapToBuyExecutedDeal(DealMessages.DealExecutedEvent event);

    @Mapping(expression = "java(event.getSaleOrderId())", target = "orderId")
    @Mapping(expression = "java(event.getSalePortfolioId())", target = "portfolioId")
    @Mapping(expression = "java(event.getCount())", target = "count")
    @Mapping(expression = "java(stringToBigDecimal(event.getLotPrice()))", target = "lotPrice")
    @Mapping(expression = "java(event.getInstrumentId())", target = "instrumentId")
    @Mapping(expression = "java(com.example.analytic_service.enums.OrderType.SALE)", target = "orderType")
    @Mapping(expression = "java(timestampToOffsetDateTime(event.getCreatedAt()))", target = "createdAt")
    ExecutedDeal mapToSaleExecutedDeal(DealMessages.DealExecutedEvent event);

    @Named("stringToBigDecimal")
    default BigDecimal stringToBigDecimal(String value) {
        return new BigDecimal(value);
    }

    @Named("timestampToOffsetDateTime")
    default OffsetDateTime timestampToOffsetDateTime(com.google.protobuf.Timestamp timestamp) {
        return OffsetDateTime.ofInstant(
                Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos()),
                ZoneOffset.UTC
        );
    }
}