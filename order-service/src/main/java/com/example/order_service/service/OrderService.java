package com.example.order_service.service;

import com.aws.protobuf.OrderCreateMessage;
import com.example.order_service.dto.CreateRequestDto;
import com.example.order_service.entity.Order;
import com.example.order_service.mapper.MapperToOrder;
import com.example.order_service.repository.OrderRepository;
import com.example.order_service.service.proto.PortfolioServiceClient;
import com.google.protobuf.Timestamp;
import com.portfolio.grpc.PortfolioServiceProto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RequiredArgsConstructor
@Service
public class OrderService {

    private final KafkaTemplate<String, OrderCreateMessage.OrderCreatedEvent> kafkaTemplate;

    private final OrderRepository orderRepository;

    private final MapperToOrder mapperToOrder;

    private final PortfolioServiceClient portfolioServiceClient;

    public Order createOrder(CreateRequestDto createRequestDto) {

        //TO DO: Добавить проверку авторизации пользователя через токен
        try {
            PortfolioServiceProto.PortfolioResponse isValidPortfolio = portfolioServiceClient.isPortfolioValid(
                    createRequestDto.user_id(),
                    createRequestDto.portfolio_id()
            );

            if (!isValidPortfolio.getIsValid()) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                        isValidPortfolio.getDescription());
            }

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "Не удалось проверить портфолио. Попробуйте позже");
        }

        Order order = mapperToOrder.mapToOrder(createRequestDto);
        order = orderRepository.save(order);

        try {
            OrderCreateMessage.OrderCreatedEvent message = OrderCreateMessage.OrderCreatedEvent.newBuilder()
                    .setOrderId(order.getOrder_id())
                    .setUserId(order.getUser_id())
                    .setPortfolioId(order.getPortfolio_id())
                    .setInstrumentName(order.getInstrument_name())
                    .setCount(order.getCount())
                    .setExecutedCount(order.getExecuted_count())
                    .setTotal(order.getTotal())
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
            log.error("Не удалось отправить в Kafka сообщение" + e.getMessage());
        }
        return order;
    }
//    public List<Order> getAllOrdersForUser(){
//        //TO DO получение токена, получение из токена user_id
//    }

}
