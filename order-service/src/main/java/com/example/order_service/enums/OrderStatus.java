package com.example.order_service.enums;

import com.aws.protobuf.OrderCreateMessage;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum OrderStatus {
    NEW(OrderCreateMessage.OrderStatus.NEW),
    PARTIALLY_EXECUTED(OrderCreateMessage.OrderStatus.PARTIALLY_EXECUTED),
    EXECUTED(OrderCreateMessage.OrderStatus.EXECUTED),
    PARTIALLY_CANCELLED(OrderCreateMessage.OrderStatus.PARTIALLY_CANCELLED),
    CANCELLED(OrderCreateMessage.OrderStatus.CANCELLED);

    private final OrderCreateMessage.OrderStatus protoStatus;

    private static final Map<OrderCreateMessage.OrderStatus, OrderStatus> PROTO_TO_JAVA =
            Arrays.stream(values())
                    .collect(Collectors.toMap(OrderStatus::getProtoStatus, Function.identity()));

    OrderStatus(OrderCreateMessage.OrderStatus protoStatus) {
        this.protoStatus = protoStatus;
    }

    public OrderCreateMessage.OrderStatus getProtoStatus() {
        return protoStatus;
    }

    public static OrderStatus fromProto(OrderCreateMessage.OrderStatus protoStatus) {
        return PROTO_TO_JAVA.get(protoStatus);
    }
}
