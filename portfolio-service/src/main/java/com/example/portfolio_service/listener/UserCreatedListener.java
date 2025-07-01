package com.example.portfolio_service.listener;

import com.aws.protobuf.UserMessages;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserCreatedListener {
    @KafkaListener(topics = "user-created", groupId = "user-created-group", containerFactory = "kafkaListenerContainerFactory")
    public void userCreatedHandler(
            @Payload UserMessages.UserCreatedEvent request,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {
        log.info("Received message: {}}", topic);
    }
}
