package com.example.user_service.service;

import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.example.user_service.dto.event.UserRegistredEvent;
import com.example.user_service.entity.User;
import com.example.user_service.mapper.MapperToUserRegistredEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaEventService {
    private final KafkaTemplate<String, UserRegistredEvent> kafkaTemplate;
    private final MapperToUserRegistredEvent mapperToUserRegistredEvent;

    @Value("${spring.kafka.topic.user-created}")
    private String userCreatedTopic;

    @Async
    @EventListener
    public void handleUserRegisteredEvent(User user) {
        try {
            UserRegistredEvent event = mapperToUserRegistredEvent.mapToUserRegistredEvent(user);
            CompletableFuture<SendResult<String, UserRegistredEvent>> future = kafkaTemplate.send(userCreatedTopic, event);
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Message sent successfully");
                } else {
                    log.error("Failed to send message: {}", ex.getMessage());
                }
            });
        } catch (Exception ex) {
            log.error("Error while sending user registration event: {}", ex.getMessage());
        }
    }
} 