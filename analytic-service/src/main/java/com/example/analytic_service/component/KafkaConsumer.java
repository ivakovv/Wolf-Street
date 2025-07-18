package com.example.analytic_service.component;

import com.aws.protobuf.DealMessages;
import com.example.analytic_service.service.AnalyticService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaConsumer {

    private final AnalyticService analyticService;

    @KafkaListener(topics = "deals", groupId = "deals-group")
    public void consumeOrderEvent(@Payload DealMessages.DealEvent event, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.info("Received message from topic: {}", topic);
        switch (event.getEventCase()) {
            case DEAL_EXECUTED -> {
                analyticService.saveDeals(event.getDealExecuted());
            }
            case EVENT_NOT_SET -> log.warn("Получено DealEvent без события");
            default -> log.error("Неизвестный тип события: {}", event.getEventCase());
        }
    }
}
