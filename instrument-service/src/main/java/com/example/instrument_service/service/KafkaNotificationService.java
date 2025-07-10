package com.example.instrument_service.service;

import com.aws.protobuf.InstrumentMessages;
import com.example.instrument_service.entity.Instrument;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaNotificationService {

    private final KafkaTemplate<String, InstrumentMessages.InstrumentEvent> kafkaTemplate;

    public void sendEvent(Instrument instrument, String event) {
        try {
            InstrumentMessages.InstrumentMessageEvent message = InstrumentMessages.InstrumentMessageEvent
                    .newBuilder()
                    .setInstrumentId(instrument.getInstrumentId())
                    .setTitle(instrument.getTitle())
                    .setTicker(instrument.getTicker())
                    .build();
            InstrumentMessages.InstrumentEvent messageEvent = null;
            switch (event) {
                case "create": {
                    messageEvent = InstrumentMessages.InstrumentEvent
                            .newBuilder()
                            .setInstrumentCreated(message)
                            .build();
                }
                case "update" : {
                    messageEvent = InstrumentMessages.InstrumentEvent
                            .newBuilder()
                            .setInstrumentUpdated(message)
                            .build();
                }
            }
            kafkaTemplate.send("instruments", messageEvent);

        } catch (Exception e) {
            log.error("Не удалось отправить сообщение об интсрументе с id = {}, event = {}",
                    instrument.getInstrumentId(), event);
        }
    }


} 