package com.example.portfolio_service.dto.profitability;

import java.util.Map;

public record PortfolioProfitabilityResponse(Map<Long, String> profitability) {
}
