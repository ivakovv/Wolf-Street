package com.example.market_data_service.mapper;

import com.aws.protobuf.DealMessages;
import com.example.market_data_service.dto.Deal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;

@Mapper(componentModel = "spring", imports = {BigDecimal.class})
public interface MapperFromEventToDeal {
    @Mapping(target = "buyOrderId", source = "buyOrderId")
    @Mapping(target = "saleOrderId", source = "saleOrderId")
    @Mapping(target = "buyPortfolioId", source = "buyPortfolioId")
    @Mapping(target = "salePortfolioId", source = "salePortfolioId")
    @Mapping(target = "instrumentId", source = "instrumentId")
    @Mapping(target = "count", source = "count")
    @Mapping(target = "lotPrice", expression = "java(new BigDecimal(dealExecutedEvent.getLotPrice()))")
    @Mapping(target = "buyOrderPrice", expression = "java(new BigDecimal(dealExecutedEvent.getBuyOrderPrice()))")
    Deal mapToDealFromEvent(DealMessages.DealExecutedEvent dealExecutedEvent);
}
