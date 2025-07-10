package com.example.instrument_service.service;

import com.example.instrument_service.dto.CreateInstrumentRequestDto;
import com.example.instrument_service.dto.UpdateInstrumentRequestDto;
import com.example.instrument_service.entity.Instrument;
import com.example.instrument_service.repository.InstrumentRepository;
import com.example.instrument_service.service.interfaces.InstrumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class InstrumentServiceImpl implements InstrumentService {
    private final InstrumentRepository instrumentRepository;

    private final KafkaNotificationService kafkaNotificationService;

    @Override
    public Instrument getInstrument(Long instrumentId) {
        return instrumentRepository.findById(instrumentId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                String.format("Инструмент с id: %d не найдена", instrumentId)));
    }

    @Override
    public List<Instrument> getInstruments() {
        return instrumentRepository.findAll();
    }

    @Override
    public Instrument createInstrument(CreateInstrumentRequestDto request) {
        instrumentRepository.findByTitle(request.title()).ifPresent(instrument -> {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Инструмент с таким названием уже существует");
                });

        instrumentRepository.findByTicker(request.ticker()).ifPresent(instrument -> {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Инструмент с таким тикером уже существует");
        });

        Instrument instrument = new Instrument();

        instrument.setTitle(request.title());
        instrument.setTicker(request.ticker());

        instrumentRepository.save(instrument);
        kafkaNotificationService.sendEvent(instrument, "create");

        return instrument;
    }

    @Override
    public Instrument updateInstrument(UpdateInstrumentRequestDto request) {
        Instrument instrument = instrumentRepository.findById(request.instrumentId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        String.format("Инструмент с ID %d не найден", request.instrumentId())
                ));
        if (instrument.getTicker().equals(request.ticker())){
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    String.format("Инструмент с тикером %s уже существует", instrument.getTicker()));
        }

        if (instrument.getTitle().equals(request.title())){
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    String.format("Инструмент с названием %s уже существует", instrument.getTitle()));
        }

        Optional.ofNullable(request.ticker()).ifPresent(instrument::setTicker);
        Optional.ofNullable(request.title()).ifPresent(instrument::setTitle);

        instrumentRepository.save(instrument);

        kafkaNotificationService.sendEvent(instrument, "update");

        return instrument;
    }

}
