package com.example.instrument_service.service.interfaces;

import com.example.instrument_service.dto.CreateInstrumentRequestDto;
import com.example.instrument_service.dto.UpdateInstrumentRequestDto;
import com.example.instrument_service.entity.Instrument;

import java.util.List;

public interface InstrumentService {
    Instrument createInstrument(CreateInstrumentRequestDto request);
    Instrument updateInstrument(UpdateInstrumentRequestDto request);
    Instrument getInstrument(Long instrumentId);
    List<Instrument> getInstruments();
}
