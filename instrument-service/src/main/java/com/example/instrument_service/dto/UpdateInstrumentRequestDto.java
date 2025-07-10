package com.example.instrument_service.dto;


public record UpdateInstrumentRequestDto(
        Long instrumentId,
        String ticker,
        String title
) {
}
