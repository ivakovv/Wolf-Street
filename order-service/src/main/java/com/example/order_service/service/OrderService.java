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
//                    createRequestDto.portfolio_id()
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

    public Order getOrderById(Long order_id){
        return orderRepository.findById(order_id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                String.format("Заявка с id: %d не найдена", order_id)));
    }

    public OrderStatusResponseDto cancelledOrder(Long order_id) {
        Order order = orderRepository.findById(order_id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                String.format("Заявка с id: %d не найдена", order_id)));
        if (Objects.equals(order.getExecuted_count(), order.getCount()) || order.getExecuted_count() == 0)
            order.setStatus(OrderStatus.CANCELLED);
        else
            order.setStatus(OrderStatus.PARTIALLY_CANCELLED);

        Order savedOrder = orderRepository.save(order);
        sendProtoMessageToKafka(order);
        return new OrderStatusResponseDto(savedOrder.getStatus());
    }

    private void sendProtoMessageToKafka(Order order) {
        try {
            OrderCreateMessage.OrderCreatedEvent message = OrderCreateMessage.OrderCreatedEvent
                    .newBuilder()
                    .setOrderId(order.getOrder_id())
                    .setUserId(order.getUser_id())
                    .setPortfolioId(order.getPortfolio_id())
                    .setInstrumentName(order.getInstrument_name())
                    .setPiecePrice((order.getPiece_price().toString()))
                    .setCount(order.getCount())
                    .setExecutedCount(order.getExecuted_count())
                    .setTotal(order.getTotal().toString())
                    .setType(order.getType().getProtoType())
                    .setStatus(order.getStatus().getProtoStatus())
                    .setCreatedAt(Timestamp.newBuilder()
                            .setSeconds(order.getCreated_at().toEpochSecond())
                            .setNanos(order.getCreated_at().getNano())
                            .build())
                    .setUpdatedAt(Timestamp.newBuilder()
                            .setSeconds(order.getUpdated_at().toEpochSecond())
                            .setNanos(order.getUpdated_at().getNano())
                            .build())
                    .build();
            kafkaTemplate.send("Orders", message);
        } catch (Exception e) {
            log.error("Не удалось отправить в Kafka сообщение " + e.getMessage());
        }
    }
}
