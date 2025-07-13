package com.example.market_data_service.controller;

import com.example.market_data_service.dto.ohlc.Ohlc;
import com.example.market_data_service.service.interfaces.RedisOhlcService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/v1/ohlc")
@RequiredArgsConstructor
public class OhlcController {
    private final RedisOhlcService redisOhlcService;

    @GetMapping("/{instrumentId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OHLC успешно получено!"),
            @ApiResponse(responseCode = "400", description = "Указан неверный временной интервал! (1m, 5m, 15m, 1h, 1d)"),
    })
    public ResponseEntity<List<Ohlc>> getOhlcForInstrument(
            @PathVariable Long instrumentId,
            @RequestParam String interval,
            @RequestParam Instant from,
            @RequestParam Instant to) {
        return ResponseEntity.ok(redisOhlcService.getOhlc(instrumentId, interval, from, to));
    }
}
