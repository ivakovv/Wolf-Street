package com.example.order_service.component;

import com.aws.protobuf.DealMessages;
import com.example.order_service.entity.Deal;
import com.example.order_service.mapper.MapperToDeal;
import com.example.order_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;


@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaConsumer {

    private final OrderService orderService;

    private final MapperToDeal mapperToDeal;

    @KafkaListener(topics = "deals", groupId = "group_id")
    public void consume(@Payload DealMessages.DealEvent event, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        switch (event.getEventCase()) {
            case DEAL_EXECUTED -> {
                DealMessages.DealExecutedEvent dealExecutedEvent = event.getDealExecuted();

                Deal deal = mapperToDeal.mapToDeal(dealExecutedEvent);
                orderService.processDeal(deal);
            }
            case EVENT_NOT_SET -> log.warn("Получено OrderEvent без события");
            default -> log.error("Неизвестный тип события: {}", event.getEventCase());
        }
    }
}