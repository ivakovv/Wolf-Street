package com.example.market_data_service.listener;

import com.aws.protobuf.OrderMessages;
import com.example.market_data_service.mapper.MapperFromEventToOrder;
import com.example.market_data_service.service.interfaces.EventProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderEventListener {
    private final EventProcessor eventProcessor;
    private final MapperFromEventToOrder mapperFromEventToOrder;
    @KafkaListener(topics = "orders", groupId = "market-data-order-group", containerFactory = "orderKafkaListenerContainerFactory")
    public void orderListener(
            @Payload OrderMessages.OrderEvent request,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic){
        log.info("Received message from topic: {}", topic);
        switch(request.getEventCase()){
            case ORDER_CREATED -> {
                log.info("Processing created order...");
                OrderMessages.OrderCreatedEvent orderCreatedEvent = request.getOrderCreated();
                eventProcessor.processCreatedOrder(mapperFromEventToOrder.mapToOrderFromEvent(orderCreatedEvent));
            }
            case ORDER_UPDATED -> {
                log.info("Processing updated order...");
                OrderMessages.OrderUpdatedEvent orderUpdatedEvent = request.getOrderUpdated();
                eventProcessor.processCancelledOrder(orderUpdatedEvent.getOrderId());
            }
            case EVENT_NOT_SET -> log.info("Received message without event");
            default -> log.error("Unknown event type: {}", request.getEventCase());
        }
    }
}
