package com.example.order_service.mapper;

import com.aws.protobuf.DealMessages;
import com.example.order_service.entity.Deal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface MapperToDeal {
    
    @Mapping(expression = "java(message.getDealId())", target = "dealId")
    @Mapping(expression = "java(message.getBuyOrderId())", target = "buyOrderId")
    @Mapping(expression = "java(message.getSaleOrderId())", target = "saleOrderId")
    @Mapping(expression = "java(message.getBuyerId())", target = "buyerId")
    @Mapping(expression = "java(message.getSellerId())", target = "sellerId")
    @Mapping(expression = "java(message.getBuyPortfolioId())", target = "buyPortfolioId")
    @Mapping(expression = "java(message.getSalePortfolioId())", target = "salePortfolioId")
    @Mapping(expression = "java(message.getCount())", target = "count")
    @Mapping(expression = "java(stringToBigDecimal(message.getLotPrice()))", target = "lotPrice")
    @Mapping(expression = "java(message.getInstrumentId())", target = "instrumentId")
    Deal mapToDeal(DealMessages.DealExecutedEvent message);

    @org.mapstruct.Named("stringToBigDecimal")
    default BigDecimal stringToBigDecimal(String value) {
        return new BigDecimal(value);
    }
}
