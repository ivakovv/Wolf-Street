package com.example.instrument_service.controller;

import com.example.instrument_service.dto.CreateInstrumentRequestDto;
import com.example.instrument_service.dto.UpdateInstrumentRequestDto;
import com.example.instrument_service.entity.Instrument;
import com.example.instrument_service.service.InstrumentServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/instruments")
public class InstrumentController {

    private final InstrumentServiceImpl instrumentService;

    @GetMapping("/{instrument_id}")
    public ResponseEntity<Instrument> getInstrument(@PathVariable(value="instrument_id") Long instrumentId){
        return ResponseEntity.ok(instrumentService.getInstrument(instrumentId));
    }

    @GetMapping()
    public ResponseEntity<List<Instrument>> getInstruments(){
        return ResponseEntity.ok(instrumentService.getInstruments());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping()
    public ResponseEntity<Instrument> createInstrument(CreateInstrumentRequestDto request) {
        return ResponseEntity.ok(instrumentService.createInstrument(request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping()
    public ResponseEntity<Instrument> updateInstrument(UpdateInstrumentRequestDto request) {
        return ResponseEntity.ok(instrumentService.updateInstrument(request));
    }

}
