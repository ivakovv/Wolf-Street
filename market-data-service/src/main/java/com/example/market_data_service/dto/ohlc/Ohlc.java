package com.example.market_data_service.dto.ohlc;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record Ohlc(BigDecimal open,
                   BigDecimal high,
                   BigDecimal low,
                   BigDecimal close,
                   Long volume,
                   OffsetDateTime openTime) {
}
