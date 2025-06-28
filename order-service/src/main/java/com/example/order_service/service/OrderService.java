package com.example.order_service.service;

import com.example.order_service.dto.CreateRequestDto;
import com.example.order_service.entity.Order;
import com.example.order_service.mapper.MapperToOrder;
import com.example.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class OrderService {

    private final OrderRepository orderRepository;

    private final MapperToOrder mapperToOrder;


    public Order createOrder(CreateRequestDto createRequestDto){
        Order order = mapperToOrder.mapToOrder(createRequestDto);
        return orderRepository.save(order);
    }



}
