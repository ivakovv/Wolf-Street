package com.example.analytic_service.enums;

import com.aws.protobuf.DealMessages;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum OrderType {
    BUY(DealMessages.OrderType.BUY),
    SALE(DealMessages.OrderType.SALE);

    private final DealMessages.OrderType protoType;

    private static final Map<DealMessages.OrderType, OrderType> PROTO_TO_JAVA =
            Arrays.stream(values())
                    .collect(Collectors.toMap(OrderType::getProtoType, Function.identity()));

    OrderType(DealMessages.OrderType protoType) {
        this.protoType = protoType;
    }

    public DealMessages.OrderType getProtoType() {
        return protoType;
    }

    public static OrderType fromProto(DealMessages.OrderType protoType) {
        return PROTO_TO_JAVA.get(protoType);
    }
}