package com.example.instrument_service.dto;

public record CreateInstrumentRequestDto(
        String ticker,
        String title
) {
}
