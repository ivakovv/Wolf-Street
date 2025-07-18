package com.example.analytic_service.controller;

import com.example.analytic_service.service.AnalyticService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AnalyticController {

    private final AnalyticService analyticService;

    @GetMapping("/profitability")
    public ResponseEntity<Map<Long, String>> getInstrumentProfitability(
            @RequestParam List<Long> instrumentIds,
            @RequestParam String period
    ) {
        Map<Long, BigDecimal> result = analyticService.getInstrumentProfitability(instrumentIds, period);
        Map<Long, String> stringResult = result.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().toString()));
        return ResponseEntity.ok(stringResult);
    }
}
