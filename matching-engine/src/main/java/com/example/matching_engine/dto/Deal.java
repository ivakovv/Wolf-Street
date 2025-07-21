package com.example.matching_engine.dto;

import java.math.BigDecimal;

public record Deal(Long buyOrderId,
                   Long saleOrderId,
                   Long buyPortfolioId,
                   Long salePortfolioId,
                   Long instrumentId,
                   Long count,
                   BigDecimal lotPrice,
                   BigDecimal saleOrderPrice,
                   BigDecimal buyOrderPrice) {
}
