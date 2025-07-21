package com.example.matching_engine.component;

import com.aws.protobuf.OrderMessages;
import com.example.matching_engine.dto.Deal;
import com.example.matching_engine.engine.interfaces.MatchingEngine;
import com.example.matching_engine.entity.Order;
import com.example.matching_engine.mapper.MapperToOrderFromEvent;
import com.example.matching_engine.service.KafkaEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaConsumer {
    private final MapperToOrderFromEvent mapperToOrderFromEvent;
    private final MatchingEngine matchingEngine;
    private final KafkaEventService kafkaEventService;

    @KafkaListener(topics = "orders", groupId = "matching-engine-group")
    public void consumeOrderEvent(@Payload OrderMessages.OrderEvent event, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.info("Received message from topic: {}", topic);
        switch (event.getEventCase()) {
            case ORDER_CREATED -> {
                log.info("order {}", event.getOrderCreated().getOrderId());
                List<Deal> deals = matchingEngine.processOrder(mapperToOrderFromEvent.mapToOrderFromEvent(event.getOrderCreated()));
                if(!deals.isEmpty()){
                    for (Deal deal : deals) {
                        kafkaEventService.handleDealExecutedEvent(deal);
                    }
                }
            }
            case ORDER_UPDATED -> {
                OrderMessages.OrderUpdatedEvent orderUpdatedEvent = event.getOrderUpdated();
                if (orderUpdatedEvent.getStatus().equals(OrderMessages.OrderStatus.CANCELLED) || orderUpdatedEvent.getStatus().equals(OrderMessages.OrderStatus.PARTIALLY_CANCELLED)){
                    try{
                        Order cancelledOrder = matchingEngine.cancelOrder(orderUpdatedEvent.getOrderId());
                        kafkaEventService.handleDealCancelledEvent(cancelledOrder);
                    } catch (IllegalArgumentException e){
                        log.error("Error while processing cancelled order: {}", e.getMessage());
                    }
                }
            }
            case EVENT_NOT_SET -> log.warn("Получено OrderEvent без события");
            default -> log.error("Неизвестный тип события: {}", event.getEventCase());
        }
    }
}
