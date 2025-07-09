package com.example.instrument_service.controller;

import com.example.instrument_service.dto.CreateInstrumentRequestDto;
import com.example.instrument_service.dto.UpdateInstrumentRequestDto;
import com.example.instrument_service.entity.Instrument;
import com.example.instrument_service.service.InstrumentServiceImpl;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Инструмент получен"),
            @ApiResponse(responseCode = "403", description = "Пользователь не авторизован"),
            @ApiResponse(responseCode = "404", description = "Инструмент не найден")
    })
    @GetMapping("/{instrument_id}")
    public ResponseEntity<Instrument> getInstrument(@PathVariable(value="instrument_id") Long instrumentId){
        return ResponseEntity.ok(instrumentService.getInstrument(instrumentId));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Инструменты получены"),
            @ApiResponse(responseCode = "403", description = "Пользователь не авторизован"),
            @ApiResponse(responseCode = "404", description = "Инструменты не найдены")
    })
    @GetMapping()
    public ResponseEntity<List<Instrument>> getInstruments(){
        return ResponseEntity.ok(instrumentService.getInstruments());
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Инструмент создан"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав"),
            @ApiResponse(responseCode = "409", description = "Ошибка при создании инструмента"),
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping()
    public ResponseEntity<Instrument> createInstrument(CreateInstrumentRequestDto request) {
        return ResponseEntity.ok(instrumentService.createInstrument(request));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Инструмент обновлен"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав"),
            @ApiResponse(responseCode = "409", description = "Ошибка при обновлении инструмента"),
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping()
    public ResponseEntity<Instrument> updateInstrument(UpdateInstrumentRequestDto request) {
        return ResponseEntity.ok(instrumentService.updateInstrument(request));
    }

}
