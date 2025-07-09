package com.example.order_service.component;

import com.aws.protobuf.DealMessages;
import com.example.order_service.entity.Deal;
import com.example.order_service.mapper.MapperToDeal;
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

    private final MapperToDeal mapperToDeal;

    @KafkaListener(topics = "deals", groupId = "group_id")
    public void consume(DealMessages.DealExecutedEvent message) {
        Deal deal = mapperToDeal.mapToDeal(message);
        orderService.processDeal(deal);
    }
}