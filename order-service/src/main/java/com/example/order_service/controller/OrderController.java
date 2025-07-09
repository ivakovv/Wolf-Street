package com.example.order_service.controller;

import com.example.order_service.dto.CreateRequestDto;
import com.example.order_service.dto.OrderStatusResponseDto;
import com.example.order_service.entity.Order;
import com.example.order_service.service.OrderService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Заявка создана"),
            @ApiResponse(responseCode = "403", description = "Произошла ошибка при проверке валидности портфолио"),
            @ApiResponse(responseCode = "503", description = "Сервис не отвечает")
    })
    @PostMapping()
    public ResponseEntity<Order> createOrder(Authentication authentication, @RequestBody CreateRequestDto orderCreateDto){
        return ResponseEntity.ok().body(orderService.createOrder(authentication, orderCreateDto));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Заявки найдены"),
            @ApiResponse(responseCode = "403", description = "Пользователь не авторизован"),
            @ApiResponse(responseCode = "404", description = "Не найдены заявки")
    })
    @GetMapping()
    public ResponseEntity<List<Order>> getAllOrdersForUser(Authentication authentication){
        return ResponseEntity.ok().body(orderService.getAllOrdersForUser(authentication));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Заявка найдена"),
            @ApiResponse(responseCode = "403", description = "Пользователь не авторизован"),
            @ApiResponse(responseCode = "404", description = "Не найдена заявка")
    })
    @GetMapping("/{order_id}")
    public ResponseEntity<Order> getAllOrdersById(@PathVariable(value="order_id") Long order_id){
        return ResponseEntity.ok().body(orderService.getOrderById(order_id));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Заявка закрыта"),
            @ApiResponse(responseCode = "403", description = "Пользователь не авторизован"),
            @ApiResponse(responseCode = "404", description = "Не найдена заявка")
    })
    @PatchMapping("/{order_id}/cancelled")
    public ResponseEntity<OrderStatusResponseDto> cancelledOrder(@PathVariable(value="order_id") Long order_id){
        return ResponseEntity.ok().body(orderService.cancelledOrder(order_id));
    }
}
