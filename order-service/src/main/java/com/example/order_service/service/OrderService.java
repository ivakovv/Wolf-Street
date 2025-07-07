package com.example.order_service.service;

import com.example.order_service.dto.CreateRequestDto;
import com.example.order_service.dto.OrderStatusResponseDto;
import com.example.order_service.entity.Deal;
import com.example.order_service.entity.Order;
import com.example.order_service.enums.OrderStatus;
import com.example.order_service.mapper.MapperToOrder;
import com.example.order_service.repository.OrderRepository;
import com.example.order_service.service.proto.PortfolioServiceClient;
import com.aws.protobuf.PortfolioServiceProto;
import com.wolfstreet.security_lib.details.JwtDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final KafkaNotificationService kafkaNotificationService;
    private final PortfolioServiceClient portfolioServiceClient;
    private final OrderRepository orderRepository;
    private final MapperToOrder mapperToOrder;

    public Order createOrder(Authentication authentication, CreateRequestDto createRequestDto) {

        JwtDetails jwtDetails = (JwtDetails) authentication.getPrincipal();


        PortfolioServiceProto.PortfolioResponse isValidPortfolio = portfolioServiceClient.isPortfolioValid(
                jwtDetails.getUserId(),
                createRequestDto
        );

        if (!isValidPortfolio.getIsValid()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    isValidPortfolio.getDescription());
        }

        Order order = mapperToOrder.mapToOrder(jwtDetails.getUserId(), createRequestDto);
        order = orderRepository.save(order);

        kafkaNotificationService.sendOrderCreatedEvent(order);

        return order;
    }

    public List<Order> getAllOrdersForUser(Authentication authentication) {
        JwtDetails jwtDetails = (JwtDetails) authentication.getPrincipal();
        return orderRepository.findByUserId(jwtDetails.getUserId());
    }

    public Order getOrderById(Long orderId) {
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
        kafkaNotificationService.sendOrderUpdatedEvent(savedOrder);
        return new OrderStatusResponseDto(savedOrder.getStatus());
    }

    /**
     * Обрабатывает сделку для двух заявок
     */
    @Transactional
    public void processDeal(Deal deal) {
        try {
            Order buyOrder = validateAndPrepareOrder(deal.getBuyOrderId(), deal.getCount(), deal.getLotPrice());
            Order saleOrder = validateAndPrepareOrder(deal.getSaleOrderId(), deal.getCount(), deal.getLotPrice());

            applyOrderChanges(buyOrder, deal.getCount(), deal.getLotPrice());
            applyOrderChanges(saleOrder, deal.getCount(), deal.getLotPrice());

            Order savedBuyOrder = orderRepository.save(buyOrder);
            Order savedSaleOrder = orderRepository.save(saleOrder);

            kafkaNotificationService.sendOrderUpdatedEvent(savedBuyOrder);
            kafkaNotificationService.sendOrderUpdatedEvent(savedSaleOrder);


        } catch (Exception e) {
            log.error("Ошибка при обработке сделки dealId={}: {}", deal.getDealId(), e.getMessage(), e);

            kafkaNotificationService.sendErrorDealMessage(deal,
                    "Ошибка обработки сделки: " + e.getMessage());
            throw e;
        }
    }

    private Order validateAndPrepareOrder(Long orderId, Long count, BigDecimal lotPrice) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException(String.format("Не найдена заявка с id %d", orderId)));

        if (order.getStatus() == OrderStatus.CANCELLED || order.getStatus() == OrderStatus.PARTIALLY_CANCELLED) {
            throw new IllegalArgumentException(String.format("Заявка с id %d закрыта", orderId));
        }

        if (order.getLotPrice().compareTo(lotPrice) > 0) {
            throw new IllegalArgumentException(String.format("Новая цена %s не может быть меньше текущей %s для заявки %d",
                    lotPrice, order.getLotPrice(), orderId));
        }

        Long newExecutedCount = order.getExecutedCount() + count;
        Long totalOrderCount = order.getCount();

        if (newExecutedCount > totalOrderCount) {
            throw new IllegalArgumentException(String.format(
                    "Заявка %d: попытка исполнить больше чем заявлено: исполнено=%d, пытаемся добавить=%d, всего в заявке=%d",
                    orderId, order.getExecutedCount(), count, totalOrderCount));
        }

        return order;
    }

    private void applyOrderChanges(Order order, Long count, BigDecimal lotPrice) {
        Long newExecutedCount = order.getExecutedCount() + count;
        Long totalOrderCount = order.getCount();

        if (newExecutedCount < totalOrderCount) {
            order.setStatus(OrderStatus.PARTIALLY_EXECUTED);
            order.setExecutedCount(newExecutedCount);
        } else if (newExecutedCount.equals(totalOrderCount)) {
            order.setStatus(OrderStatus.EXECUTED);
            order.setExecutedCount(totalOrderCount);
        }

        BigDecimal executionAmount = BigDecimal.valueOf(count)
                .multiply(lotPrice)
                .setScale(4, RoundingMode.HALF_UP);

        order.setExecutedTotal(order.getExecutedTotal().add(executionAmount));
    }

}
