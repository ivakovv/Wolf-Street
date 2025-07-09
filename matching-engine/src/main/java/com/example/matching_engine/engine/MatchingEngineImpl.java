package com.example.matching_engine.engine;

import com.example.matching_engine.dto.Deal;
import com.example.matching_engine.dto.enums.OrderStatus;
import com.example.matching_engine.dto.enums.OrderType;
import com.example.matching_engine.engine.interfaces.MatchingEngine;
import com.example.matching_engine.entity.Order;
import com.example.matching_engine.repository.OrderBookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@Component
@RequiredArgsConstructor
public class MatchingEngineImpl implements MatchingEngine {
    private final int LIMIT_FETCH = 5;
    private final OrderBookRepository orderBookRepository;

    @Override
    @Transactional
    public List<Deal> processOrder(Order order) {
        List<Deal> deals = new ArrayList<>();
        matchOrder(order, deals);
        return deals;
    }

    @Override
    @Transactional
    public Order cancelOrder(Long orderId) {
        Order orderForCancel = orderBookRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException(String.format("Order:%d doesn't exists", orderId)));
        if (orderForCancel.getOrderStatus().equals(OrderStatus.CANCELLED) || orderForCancel.getOrderStatus().equals(OrderStatus.EXECUTED)) {
            throw new IllegalArgumentException(String.format("Order: %d already cancelled or executed", orderId));
        }
        orderForCancel.setOrderStatus(OrderStatus.CANCELLED);
        orderBookRepository.save(orderForCancel);
        return orderForCancel;
    }

    private void matchOrder(Order order, List<Deal> deals) {
        long remains = order.getCount();
        while (remains > 0) {
            List<Order> matchedOrders = getMatchingOrders(order);
            log.info("Matched orders: {}", matchedOrders);
            if (matchedOrders.isEmpty()) {
                orderBookRepository.save(order);
                break;
            }
            for (Order matchedOrder : matchedOrders) {
                if (remains == 0) break;
                long matchedCount = Math.min(remains, matchedOrder.getCount());
                Deal deal = createDeal(order, matchedOrder, matchedCount, matchedOrder.getLotPrice());
                deals.add(deal);
                matchedOrder.setCount(matchedOrder.getCount() - matchedCount);
                if (matchedOrder.getCount() == 0) {
                    matchedOrder.setOrderStatus(OrderStatus.EXECUTED);
                } else {
                    matchedOrder.setOrderStatus(OrderStatus.PARTIALLY_EXECUTED);
                }
                remains -= matchedCount;
                orderBookRepository.save(matchedOrder);
            }
        }
        if (remains == 0) {
            order.setOrderStatus(OrderStatus.EXECUTED);
        } else {
            order.setCount(remains);
            order.setOrderStatus(OrderStatus.PARTIALLY_EXECUTED);
        }
        orderBookRepository.save(order);
    }

    private List<Order> getMatchingOrders(Order order) {
        if (order.getOrderType().equals(OrderType.SALE)) {
            return orderBookRepository.findBuyOrdersForSell(
                    order.getPortfolioId(), order.getInstrumentId(), order.getLotPrice(), LIMIT_FETCH);
        } else {
            return orderBookRepository.findSellOrdersForBuy(
                    order.getPortfolioId(), order.getInstrumentId(), order.getLotPrice(), LIMIT_FETCH);
        }
    }

    private Deal createDeal(Order order, Order matchedOrder, long count, BigDecimal price) {
        Order buyOrder = order.getOrderType() == OrderType.BUY ? order : matchedOrder;
        Order saleOrder = order.getOrderType() == OrderType.SALE ? order : matchedOrder;
        return new Deal(
                buyOrder.getId(),
                saleOrder.getId(),
                buyOrder.getPortfolioId(),
                saleOrder.getPortfolioId(),
                buyOrder.getInstrumentId(),
                count,
                price,
                buyOrder.getLotPrice()
        );
    }
}