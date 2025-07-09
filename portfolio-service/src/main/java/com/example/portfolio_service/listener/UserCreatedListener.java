package com.example.portfolio_service.listener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import com.aws.protobuf.UserMessages;
import com.example.portfolio_service.service.PortfolioServiceImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserCreatedListener {
    private final PortfolioServiceImpl portfolioServiceImpl;
    @KafkaListener(topics = "user-created", groupId = "user-created-group", containerFactory = "userKafkaListenerContainerFactory")
    public void userCreatedHandler(
            @Payload UserMessages.UserCreatedEvent request,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.info("Received message from topic: {}, user id: {}", topic, request.getId());
        try {
            portfolioServiceImpl.createPortfoliosForNewUser(request);
            log.info("Portfolio created successfully, user id: {}", request.getId());
        } catch (Exception e) {
            log.error("Error creating portfolio: {}", e.getMessage(), e);
        }
    }
}
