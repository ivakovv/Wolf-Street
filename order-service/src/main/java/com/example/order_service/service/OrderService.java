package com.example.order_service.service;

import com.aws.protobuf.OrderCreateMessage;
import com.example.order_service.dto.CreateRequestDto;
import com.example.order_service.dto.OrderStatusResponseDto;
import com.example.order_service.entity.Order;
import com.example.order_service.enums.OrderStatus;
import com.example.order_service.mapper.MapperToOrder;
import com.example.order_service.repository.OrderRepository;
import com.example.order_service.service.proto.PortfolioServiceClient;
import com.google.protobuf.Timestamp;
import com.aws.protobuf.PortfolioServiceProto;
import com.wolfstreet.security_lib.details.JwtDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Service
public class OrderService {

    private final KafkaTemplate<String, OrderCreateMessage.OrderCreatedEvent> kafkaTemplate;

    private final OrderRepository orderRepository;

    private final MapperToOrder mapperToOrder;

    private final PortfolioServiceClient portfolioServiceClient;

    public Order createOrder(Authentication authentication, CreateRequestDto createRequestDto) {

        JwtDetails jwtDetails = (JwtDetails)authentication.getPrincipal();

//        try {
//            PortfolioServiceProto.PortfolioResponse isValidPortfolio = portfolioServiceClient.isPortfolioValid(
//                    jwtDetails.getUserId(),
//                    createRequestDto
//            );
//
//            if (!isValidPortfolio.getIsValid()) {
//                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
//                        isValidPortfolio.getDescription());
//            }
//
//        } catch (Exception e) {
//            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
//                    "Не удалось проверить портфолио. Попробуйте позже");
//        }

        Order order = mapperToOrder.mapToOrder(jwtDetails.getUserId(), createRequestDto);
        order = orderRepository.save(order);

        sendProtoMessageToKafka(order);

        return order;
    }

    public List<Order> getAllOrdersForUser(Authentication authentication){
        JwtDetails jwtDetails = (JwtDetails)authentication.getPrincipal();
        return orderRepository.findByUserId(jwtDetails.getUserId());
    }

    public Order getOrderById(Long orderId){
        return orderRepository.findById(orderId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                String.format("Заявка с id: %d не найдена", orderId)));
    }

    public OrderStatusResponseDto cancelledOrder(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                String.format("Заявка с id: %d не найдена", orderId)));
        if (Objects.equals(order.getExecutedCount(), order.getCount()) || order.getExecutedCount() == 0)
            order.setStatus(OrderStatus.CANCELLED);
        else
            order.setStatus(OrderStatus.PARTIALLY_CANCELLED);

        Order savedOrder = orderRepository.save(order);
        sendProtoMessageToKafka(savedOrder);
        return new OrderStatusResponseDto(savedOrder.getStatus());
    }

    private void sendProtoMessageToKafka(Order order) {
        try {
            OrderCreateMessage.OrderCreatedEvent message = OrderCreateMessage.OrderCreatedEvent
                    .newBuilder()
                    .setOrderId(order.getOrderId())
                    .setUserId(order.getUserId())
                    .setPortfolioId(order.getPortfolioId())
                    .setInstrumentId(order.getInstrumentId())
                    .setCount(order.getCount())
                    .setLotPrice(order.getLotPrice().toString())
                    .setType(order.getType().getProtoType())
                    .setStatus(order.getStatus().getProtoStatus())
                    .setCreatedAt(Timestamp.newBuilder()
                            .setSeconds(order.getCreatedAt().toEpochSecond())
                            .setNanos(order.getCreatedAt().getNano())
                            .build())
                    .build();
            kafkaTemplate.send("Orders", message);
        } catch (Exception e) {
            log.error("Не удалось отправить в Kafka сообщение " + e.getMessage());
        }
    }
}
