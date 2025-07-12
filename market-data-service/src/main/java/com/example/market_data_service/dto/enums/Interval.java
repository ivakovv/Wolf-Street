package com.example.market_data_service.dto.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;

@Getter
@RequiredArgsConstructor
public enum Interval {
    ONE_MINUTE(Duration.ofMinutes(1), "1m"),
    FIVE_MINUTES(Duration.ofMinutes(5), "5m"),
    FIFTEEN_MINUTES(Duration.ofMinutes(15), "15m"),
    ONE_HOUR(Duration.ofHours(1), "1h"),
    ONE_DAY(Duration.ofDays(1), "1d");

    private final Duration duration;
    private final String code;

    private static final Map<String, Interval> STRING_TO_ENUM = Map.of(
            "1m", ONE_MINUTE,
            "5m", FIVE_MINUTES,
            "15m", FIFTEEN_MINUTES,
            "1h", ONE_HOUR,
            "1d", ONE_DAY
    );

    @Override
    public String toString() {
        return code;
    }

    public static Optional<Interval> fromString(String str) {
        return Optional.ofNullable(STRING_TO_ENUM.get(str.toLowerCase()));
    }
}
