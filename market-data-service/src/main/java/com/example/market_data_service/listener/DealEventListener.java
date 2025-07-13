package com.example.market_data_service.listener;

import com.aws.protobuf.DealMessages;
import com.example.market_data_service.mapper.MapperFromEventToDeal;
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
public class DealEventListener {
    private final EventProcessor eventProcessor;
    private final MapperFromEventToDeal mapperFromEventToDeal;
    @KafkaListener(topics = "deals", groupId = "market-data-deal-group", containerFactory = "dealKafkaListenerContainerFactory")
    public void dealExecutedHandler(
            @Payload DealMessages.DealEvent request,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.info("Received message from topic: {}, event: {}", topic, request.getEventCase());
        switch (request.getEventCase()){
            case DEAL_EXECUTED -> {
                log.info("processing executed deal...");
                DealMessages.DealExecutedEvent dealExecutedEvent = request.getDealExecuted();
                log.info("payload: {}", dealExecutedEvent);
                eventProcessor.processExecutedDeal(mapperFromEventToDeal.mapToDealFromEvent(dealExecutedEvent));
            }
            case EVENT_NOT_SET -> log.warn("Received a DealEvent with no event");
            default -> log.error("Received an unknown event type: {}", request.getEventCase());
        }
    }
}
