package com.example.matching_engine.mapper;

import com.aws.protobuf.OrderMessages;
import com.example.matching_engine.dto.Order;
import com.example.matching_engine.dto.enums.OrderStatus;
import com.example.matching_engine.dto.enums.OrderType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Mapper(componentModel = "spring")
public interface MapperToOrderFromEvent {
    @Mapping(target = "orderId", source = "orderId")
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "portfolioId", source = "portfolioId")
    @Mapping(target = "instrumentId", source = "instrumentId")
    @Mapping(target = "count", source = "count")
    @Mapping(target = "lotPrice", expression = "java(new BigDecimal(orderCreatedEvent.getLotPrice()))")
    @Mapping(target = "type", source = "type", qualifiedByName = "mapOrderType")
    @Mapping(target = "status", source = "status", qualifiedByName = "mapOrderStatus")
    @Mapping(target = "createdAt", expression = "java(toOffsetDateTime(orderCreatedEvent.getCreatedAt()))")
    Order mapToOrderFromEvent(OrderMessages.OrderCreatedEvent orderCreatedEvent);
    default OffsetDateTime toOffsetDateTime(com.google.protobuf.Timestamp timestamp) {
        if (timestamp == null) {
            return null;
        }
        return Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos())
                .atOffset(ZoneOffset.UTC);
    }
    @Named("mapOrderType")
    default OrderType mapOrderType(OrderMessages.OrderType type){
        if(type == OrderMessages.OrderType.UNRECOGNIZED){
            throw new IllegalArgumentException("Cannot map UNRECOGNIZED OrderType");
        }
        return OrderType.valueOf(type.name());
    }

    @Named("mapOrderStatus")
    default OrderStatus mapOrderStatus(OrderMessages.OrderStatus status) {
        if (status == OrderMessages.OrderStatus.UNRECOGNIZED) {
            throw new IllegalArgumentException("Cannot map UNRECOGNIZED OrderStatus");
        }
        return OrderStatus.valueOf(status.name());
    }
}
