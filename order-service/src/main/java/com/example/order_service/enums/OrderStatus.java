package com.example.order_service.enums;

import com.aws.protobuf.OrderMessages;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum OrderStatus {
    NEW(OrderMessages.OrderStatus.NEW),
    PARTIALLY_EXECUTED(OrderMessages.OrderStatus.PARTIALLY_EXECUTED),
    EXECUTED(OrderMessages.OrderStatus.EXECUTED),
    PARTIALLY_CANCELLED(OrderMessages.OrderStatus.PARTIALLY_CANCELLED),
    CANCELLED(OrderMessages.OrderStatus.CANCELLED);

    private final OrderMessages.OrderStatus protoStatus;

    private static final Map<OrderMessages.OrderStatus, OrderStatus> PROTO_TO_JAVA =
            Arrays.stream(values())
                    .collect(Collectors.toMap(OrderStatus::getProtoStatus, Function.identity()));

    OrderStatus(OrderMessages.OrderStatus protoStatus) {
        this.protoStatus = protoStatus;
    }

    public OrderMessages.OrderStatus getProtoStatus() {
        return protoStatus;
    }

    public static OrderStatus fromProto(OrderMessages.OrderStatus protoStatus) {
        return PROTO_TO_JAVA.get(protoStatus);
    }
}
