package com.example.matching_engine.component;

import com.aws.protobuf.OrderMessages;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaConsumer {

    @KafkaListener(topics = "orders", groupId = "orders-group")
    public void consumeOrderEvent(
            @Payload OrderMessages.OrderEvent event,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        switch (event.getEventCase()) {
            case ORDER_CREATED -> {
            }
            case ORDER_UPDATED -> {
            }
            case EVENT_NOT_SET -> log.warn("Получено OrderEvent без события");
            default -> log.error("Неизвестный тип события: {}", event.getEventCase());
        }
    }

}
