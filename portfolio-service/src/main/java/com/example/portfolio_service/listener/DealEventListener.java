package com.example.portfolio_service.listener;

import com.aws.protobuf.DealMessages;
import com.example.portfolio_service.enums.OrderType;
import com.example.portfolio_service.service.interfaces.PortfolioValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class DealEventListener {
    private final PortfolioValidationService portfolioValidationService;
    @KafkaListener(topics = "deals", groupId = "executed-deal-group", containerFactory = "dealKafkaListenerContainerFactory")
    public void dealExecutedHandler(
            @Payload DealMessages.DealEvent request,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.info("Received message from topic: {}, event: {}", topic, request.getEventCase());
        switch (request.getEventCase()){
            case DEAL_EXECUTED -> {
                log.info("processing executed deal...");
                DealMessages.DealExecutedEvent dealExecutedEvent = request.getDealExecuted();
                portfolioValidationService.processExecutedDeal(
                        dealExecutedEvent.getBuyPortfolioId(),
                        dealExecutedEvent.getSalePortfolioId(),
                        dealExecutedEvent.getInstrumentId(),
                        dealExecutedEvent.getCount(),
                        new BigDecimal(dealExecutedEvent.getLotPrice()),
                        new BigDecimal(dealExecutedEvent.getBuyOrderPrice()));
            }
            case DEAL_CANCELLED -> {
                log.info("processing cancelled deal...");
                DealMessages.DealCancelledEvent dealCancelledEvent = request.getDealCancelled();
                portfolioValidationService.processCancelledDeal(
                        dealCancelledEvent.getPortfolioId(),
                        dealCancelledEvent.getInstrumentId(),
                        dealCancelledEvent.getCount(),
                        new BigDecimal(dealCancelledEvent.getLotPrice()),
                        OrderType.valueOf(dealCancelledEvent.getOrderType().name())
                );
            }
            case EVENT_NOT_SET -> log.warn("Received a DealEvent with no event");
            default -> log.error("Received an unknown event type: {}", request.getEventCase());
        }
    }
}
