package com.example.order_service.service;

import com.aws.protobuf.OrderMessages;
import com.aws.protobuf.DealMessages;
import com.example.order_service.entity.Deal;
import com.example.order_service.entity.Order;
import com.google.protobuf.Timestamp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaNotificationService {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    
    private static final String ORDERS_TOPIC = "orders";

    public void sendOrderCreatedEvent(Order order) {
        try {
            OrderMessages.OrderCreatedEvent message = OrderMessages.OrderCreatedEvent
                    .newBuilder()
                    .setOrderId(order.getOrderId())
                    .setUserId(order.getUserId())
                    .setPortfolioId(order.getPortfolioId())
                    .setInstrumentId(order.getInstrumentId())
                    .setCount(order.getCount())
                    .setLotPrice(order.getLotPrice().toString())
                    .setType(order.getType().getProtoType())
                    .setStatus(order.getStatus().getProtoStatus())
                    .setCreatedAt(buildTimestamp(order.getCreatedAt()))
                    .build();
                    
            kafkaTemplate.send(ORDERS_TOPIC, message);
        } catch (Exception e) {
            log.error("Не удалось отправить сообщение о создании заявки orderId={}: {}", 
                order.getOrderId(), e.getMessage(), e);
        }
    }

    public void sendOrderUpdatedEvent(Order order) {
        try {
            OrderMessages.OrderUpdatedEvent message = OrderMessages.OrderUpdatedEvent
                    .newBuilder()
                    .setOrderId(order.getOrderId())
                    .setUserId(order.getUserId())
                    .setPortfolioId(order.getPortfolioId())
                    .setInstrumentId(order.getInstrumentId())
                    .setCount(order.getCount())
                    .setExecutedCount(order.getExecutedCount())
                    .setLotPrice(order.getLotPrice().toString())
                    .setExecutedTotal(order.getExecutedTotal().toString())
                    .setType(order.getType().getProtoType())
                    .setStatus(order.getStatus().getProtoStatus())
                    .setCreatedAt(buildTimestamp(order.getCreatedAt()))
                    .setUpdatedAt(buildTimestamp(order.getUpdatedAt()))
                    .build();
                    
            kafkaTemplate.send(ORDERS_TOPIC, message);
        } catch (Exception e) {
            log.error("Не удалось отправить сообщение об обновлении заявки orderId={}: {}", 
                order.getOrderId(), e.getMessage(), e);
        }
    }

    public void sendErrorDealMessage(Deal deal, String description) {
        try {
            DealMessages.DealErrorEvent message = DealMessages.DealErrorEvent
                    .newBuilder()
                    .setDealId(deal.getDealId())
                    .setBuyOrderId(deal.getBuyOrderId())
                    .setSaleOrderId(deal.getSaleOrderId())
                    .setBuyerId(deal.getBuyerId())
                    .setSellerId(deal.getSellerId())
                    .setBuyPortfolioId(deal.getBuyPortfolioId())
                    .setSalePortfolioId(deal.getSalePortfolioId())
                    .setInstrumentId(deal.getInstrumentId())
                    .setDescription(description)
                    .build();
                    
            kafkaTemplate.send("deals-errors", message);
        } catch (Exception e) {
            log.error("Не удалось отправить сообщение об ошибке сделки dealId={}: {}", 
                deal.getDealId(), e.getMessage(), e);
        }
    }

    private Timestamp buildTimestamp(OffsetDateTime dateTime) {
        return Timestamp.newBuilder()
                .setSeconds(dateTime.toEpochSecond())
                .setNanos(dateTime.getNano())
                .build();
    }
} 