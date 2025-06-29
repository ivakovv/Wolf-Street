package com.example.order_service.mapper;

import com.example.order_service.dto.CreateRequestDto;
import com.example.order_service.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MapperToOrder {
    @Mapping(target = "user_id", source = "user_id")
    @Mapping(target = "portfolio_id", source = "portfolio_id")
    @Mapping(target = "total", source = "total")
    @Mapping(target = "executed_count", source = "executed_count")
    @Mapping(target = "instrument_name", source = "instrument_name")
    @Mapping(target = "count", source = "count")
    @Mapping(target = "type", source = "type")
    @Mapping(target = "status", expression = "java(com.example.order_service.enums.OrderStatus.NEW)")
    @Mapping(target = "order_id", ignore = true)
    @Mapping(target = "created_at", ignore = true)
    @Mapping(target = "updated_at", ignore = true)
    Order mapToOrder(CreateRequestDto createRequestDto);
}
