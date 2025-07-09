package com.example.matching_engine.service;

import com.aws.protobuf.DealMessages;
import com.example.matching_engine.dto.Deal;
import com.example.matching_engine.entity.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaEventService {
    @Value("${spring.kafka.topic.deal-topic}")
    private String dealTopic;
    private final KafkaTemplate<String, DealMessages.DealEvent> kafkaTemplate;

    @Async
    @EventListener
    public void handleDealExecutedEvent(Deal deal){
        DealMessages.DealExecutedEvent executedEvent = DealMessages.DealExecutedEvent.newBuilder()
                .setDealId(deal.dealId())
                .setBuyOrderId(deal.buyOrderId())
                .setSaleOrderId(deal.saleOrderId())
                .setBuyPortfolioId(deal.buyPortfolioId())
                .setSalePortfolioId(deal.salePortfolioId())
                .setCount(deal.count())
                .setLotPrice(deal.lotPrice().toString())
                .setBuyOrderPrice(deal.buyOrderPrice().toString())
                .setInstrumentId(deal.instrumentId())
                .build();
        DealMessages.DealEvent message = DealMessages.DealEvent.newBuilder()
                .setDealExecuted(executedEvent)
                .build();
        sendKafkaMessage(message);
    }

    @Async
    @EventListener
    public void handleDealCancelledEvent(Order order){
        DealMessages.DealCancelledEvent cancelledEvent = DealMessages.DealCancelledEvent.newBuilder()
                .setOrderId(order.getId())
                .setPortfolioId(order.getPortfolioId())
                .setCount(order.getCount())
                .setLotPrice(order.getLotPrice().toString())
                .setInstrumentId(order.getInstrumentId())
                .setOrderType(DealMessages.OrderType.valueOf(order.getOrderType().name()))
                .build();
        DealMessages.DealEvent message = DealMessages.DealEvent.newBuilder()
                .setDealCancelled(cancelledEvent)
                .build();
        sendKafkaMessage(message);


    }

    private void sendKafkaMessage(DealMessages.DealEvent message){
        CompletableFuture<SendResult<String, DealMessages.DealEvent>> future = kafkaTemplate.send(dealTopic, message);
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Message sent successfully");
            } else {
                log.error("Failed to send message: {}", ex.getMessage());
            }
        });
    }
}
