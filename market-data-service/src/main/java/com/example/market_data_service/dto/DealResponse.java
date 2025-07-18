package com.example.market_data_service.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record DealResponse (Long instrumentId, Long count, BigDecimal lotPrice, OffsetDateTime createdAt){
    public static DealResponse fromDeal(Deal deal) {
        return new DealResponse(
                deal.instrumentId(),
                deal.count(),
                deal.lotPrice(),
                deal.createdAt()
        );
    }
}
