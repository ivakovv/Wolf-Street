package com.example.user_service.service;

import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.aws.protobuf.UserMessages;
import com.example.user_service.entity.User;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaEventService {
    private final KafkaTemplate<String, UserMessages.UserCreatedEvent> kafkaTemplate;

    @Value("${spring.kafka.topic.user-created}")
    private String userCreatedTopic;

    @Async
    @EventListener
    public void handleUserRegisteredEvent(User user) {
            UserMessages.UserCreatedEvent message = UserMessages.UserCreatedEvent.newBuilder()
                    .setId(user.getId())
                    .setUsername(user.getUsername())
                    .setFirstname(user.getFirstname() == null ? "" : user.getFirstname())
                    .setLastname(user.getLastname() == null ? "" : user.getLastname())
                    .setEmail(user.getEmail())
                    .setPhone(user.getPhone() == null ? "" : user.getPhone())
                    .build();
            CompletableFuture<SendResult<String, UserMessages.UserCreatedEvent>> future = kafkaTemplate.send(userCreatedTopic, message);
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Message sent successfully");
                } else {
                    log.error("Failed to send message: {}", ex.getMessage());
                }
            });
        }
}