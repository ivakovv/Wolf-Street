package com.example.portfolio_service.dto.profitability;

import java.util.List;

public record PortfolioProfitabilityRequest(List<Long> instrumentIds) {
}
