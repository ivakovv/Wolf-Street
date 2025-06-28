package com.example.order_service.mapper;

import com.example.order_service.dto.CreateRequestDto;
import com.example.order_service.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MapperToOrder {
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "portfolioId", source = "portfolioId")
    @Mapping(target = "instrument_name", source = "instrument_name")
    @Mapping(target = "count", source = "count")
    @Mapping(target = "type", source = "type")
    @Mapping(target = "status", expression = "java(com.example.order_service.enums.OrderStatus.NEW)")
    Order mapToOrder(CreateRequestDto createRequestDto);
}
