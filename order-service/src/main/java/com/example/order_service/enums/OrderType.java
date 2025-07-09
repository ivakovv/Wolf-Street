package com.example.order_service.enums;

import com.aws.protobuf.OrderMessages;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum OrderType {
    BUY(OrderMessages.OrderType.BUY),
    SALE(OrderMessages.OrderType.SALE);

    private final OrderMessages.OrderType protoType;

    private static final Map<OrderMessages.OrderType, OrderType> PROTO_TO_JAVA =
            Arrays.stream(values())
                    .collect(Collectors.toMap(OrderType::getProtoType, Function.identity()));

    OrderType(OrderMessages.OrderType protoType) {
        this.protoType = protoType;
    }

    public OrderMessages.OrderType getProtoType() {
        return protoType;
    }

    public static OrderType fromProto(OrderMessages.OrderType protoType) {
        return PROTO_TO_JAVA.get(protoType);
    }
}
