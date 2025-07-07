package com.example.matching_engine.component;

import com.aws.protobuf.OrderMessages;
import com.example.matching_engine.dto.Deal;
import com.example.matching_engine.dto.Order;
import com.example.matching_engine.dto.enums.OrderType;
import com.example.matching_engine.engine.interfaces.MatchingEngine;
import com.example.matching_engine.mapper.MapperToOrderFromEvent;
import com.example.matching_engine.service.KafkaEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaConsumer {
    private final MapperToOrderFromEvent mapperToOrderFromEvent;
    private final MatchingEngine matchingEngine;
    private final KafkaEventService kafkaEventService;

    @KafkaListener(topics = "orders", groupId = "orders-group")
    public void consumeOrderEvent(@Payload OrderMessages.OrderEvent event, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.info("Received message from topic: {}", topic);
        switch (event.getEventCase()) {
            case ORDER_CREATED -> {
                List<Deal> deals = matchingEngine.processOrder(mapperToOrderFromEvent.mapToOrderFromEvent(event.getOrderCreated()));
                if(!deals.isEmpty()){
                    for (Deal deal : deals) {
                        kafkaEventService.handleDealExecutedEvent(deal);
                    }
                }
            }
            case ORDER_UPDATED -> {
                OrderMessages.OrderUpdatedEvent orderUpdatedEvent = event.getOrderUpdated();
                if (orderUpdatedEvent.getStatus().equals(OrderMessages.OrderStatus.CANCELLED)){
                    Order cancelledOrder = matchingEngine.cancelOrder(
                            orderUpdatedEvent.getOrderId(),
                            OrderType.valueOf(orderUpdatedEvent.getType().name()),
                            orderUpdatedEvent.getInstrumentId());
                    kafkaEventService.handleDealCancelledEvent(cancelledOrder);
                }
            }
            case EVENT_NOT_SET -> log.warn("Получено OrderEvent без события");
            default -> log.error("Неизвестный тип события: {}", event.getEventCase());
        }
    }
}
