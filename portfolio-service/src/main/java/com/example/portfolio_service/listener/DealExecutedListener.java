package com.example.portfolio_service.listener;

import com.aws.protobuf.DealMessages;
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
public class DealExecutedListener {
    private final PortfolioValidationService portfolioValidationService;
    @KafkaListener(topics = "trades", groupId = "executed-deal-group", containerFactory = "dealKafkaListenerContainerFactory")
    public void dealExecutedHandler(
            @Payload DealMessages.ExecutedDealMessage request,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.info("Received message from topic: {}, dealId: {}", topic, request.getDealId());
        portfolioValidationService.processDeal(
                request.getBuyerId(),
                request.getSellerId(),
                request.getBuyPortfolioId(),
                request.getSalePortfolioId(),
                request.getInstrumentId(),
                request.getCount(),
                new BigDecimal(request.getLotPrice()));
    }
}
