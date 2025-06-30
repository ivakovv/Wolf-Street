package com.example.order_service.enums;

import com.aws.protobuf.OrderCreateMessage;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum OrderType {
    BUY(OrderCreateMessage.OrderType.BUY),
    SALE(OrderCreateMessage.OrderType.SALE);

    private final OrderCreateMessage.OrderType protoType;

    private static final Map<OrderCreateMessage.OrderType, OrderType> PROTO_TO_JAVA =
            Arrays.stream(values())
                    .collect(Collectors.toMap(OrderType::getProtoType, Function.identity()));

    OrderType(OrderCreateMessage.OrderType protoType) {
        this.protoType = protoType;
    }

    public OrderCreateMessage.OrderType getProtoType() {
        return protoType;
    }

    public static OrderType fromProto(OrderCreateMessage.OrderType protoType) {
        return PROTO_TO_JAVA.get(protoType);
    }
}
