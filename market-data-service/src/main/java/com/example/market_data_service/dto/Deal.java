package com.example.market_data_service.dto;

import java.math.BigDecimal;

public record Deal(Long buyOrderId,
                   Long saleOrderId,
                   Long buyPortfolioId,
                   Long salePortfolioId,
                   Long instrumentId,
                   Long count,
                   BigDecimal lotPrice,
                   BigDecimal buyOrderPrice) {
}
