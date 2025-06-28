package com.example.order_service.controller.exception;

import com.example.order_service.dto.CreateRequestDto;
import com.example.order_service.service.OrderService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping()
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Заявка создана"),
            @ApiResponse(responseCode = "404"),
            @ApiResponse(responseCode = "409")
    })
    public ResponseEntity<String> createOrder(@RequestBody CreateRequestDto orderCreateDto){

        orderService.createOrder(orderCreateDto);
        return ResponseEntity.ok().body("Заявка успешно создана");
    }

    @PatchMapping("/{id}")
    public ResponseEntity<String> updateOrder(){
        return ResponseEntity.ok().body("...");
    }
}
