package com.example.order_service.mapper;

import com.example.order_service.dto.CreateRequestDto;
import com.example.order_service.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MapperToOrder {
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "portfolioId", source = "createRequestDto.portfolioId")
    @Mapping(target = "lotPrice", source = "createRequestDto.lotPrice")
    @Mapping(target = "instrumentId", source = "createRequestDto.instrumentId")
    @Mapping(target = "count", source = "createRequestDto.count")
    @Mapping(target = "type", source = "createRequestDto.type")
    @Mapping(target = "status", expression = "java(com.example.order_service.enums.OrderStatus.NEW)")
    @Mapping(target = "orderId", ignore = true)
    @Mapping(target = "executedCount", ignore = true)
    @Mapping(target = "executedTotal", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Order mapToOrder(Long userId, CreateRequestDto createRequestDto);
}
