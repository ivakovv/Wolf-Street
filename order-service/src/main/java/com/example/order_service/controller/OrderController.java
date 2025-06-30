package com.example.order_service.controller;

import com.example.order_service.dto.CreateRequestDto;
import com.example.order_service.entity.Order;
import com.example.order_service.service.OrderService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping()
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Заявка создана"),
            @ApiResponse(responseCode = "403", description = "Произошла ошибка при проверке валидности портфолио"),
            @ApiResponse(responseCode = "503", description = "Сервис не отвечает")
    })
    public ResponseEntity<Order> createOrder(@RequestBody CreateRequestDto orderCreateDto){
        return ResponseEntity.ok().body(orderService.createOrder(orderCreateDto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<String> updateOrder(){
        return ResponseEntity.ok().body("...");
    }

//    @GetMapping()
//    public ResponseEntity<List<Order>> getAllOrdersForUser(){
//        return ResponseEntity.ok().body(orderService.getAllOrdersForUser());
//    }
}
