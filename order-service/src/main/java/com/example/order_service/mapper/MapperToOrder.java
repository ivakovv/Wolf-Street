package com.example.order_service.mapper;

import com.example.order_service.dto.CreateRequestDto;
import com.example.order_service.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MapperToOrder {
    @Mapping(target = "user_id", source = "user_id")
    @Mapping(target = "portfolio_id", source = "createRequestDto.portfolio_id")
    @Mapping(target = "total", source = "createRequestDto.total")
    @Mapping(target = "executed_count", source = "createRequestDto.executed_count")
    @Mapping(target = "instrument_name", source = "createRequestDto.instrument_name")
    @Mapping(target = "piece_price", source = "createRequestDto.piece_price")
    @Mapping(target = "count", source = "createRequestDto.count")
    @Mapping(target = "type", source = "createRequestDto.type")
    @Mapping(target = "status", expression = "java(com.example.order_service.enums.OrderStatus.NEW)")
    @Mapping(target = "order_id", ignore = true)
    @Mapping(target = "created_at", ignore = true)
    @Mapping(target = "updated_at", ignore = true)
    Order mapToOrder(Long user_id, CreateRequestDto createRequestDto);
}
