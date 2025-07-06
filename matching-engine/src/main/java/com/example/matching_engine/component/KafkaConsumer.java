package com.example.matching_engine.component;

import com.aws.protobuf.OrderMessages;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaConsumer {


    @KafkaListener(topics = "orders", groupId = "orders-group")
    public void consumeOrderCreated(OrderMessages.OrderCreatedEvent message) {

    }

    @KafkaListener(topics = "orders", groupId = "orders-group")
    public void consumeOrderUpdated(OrderMessages.OrderUpdatedEvent message) {

    }

}
