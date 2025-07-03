package com.example.order_service.component;

import com.aws.protobuf.DealMessages;
import com.example.order_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaConsumer {

    private final OrderService orderService;

    @KafkaListener(topics = "Deals", groupId = "group_id")
    public void consume(DealMessages.ExecutedDealMessage message) {
        orderService.processDeal(
            message.getDealId(),
            message.getBuyOrderId(),
            message.getSaleOrderId(),
            message.getCount(),
            new BigDecimal(message.getLotPrice())
        );
    }
}