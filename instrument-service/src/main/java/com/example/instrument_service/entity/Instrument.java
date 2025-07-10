package com.example.instrument_service.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "instruments")
public class Instrument {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "instrument_id")
    private Long instrumentId;

    @Column(name = "ticker", nullable = false)
    private String ticker;

    @Column(name = "title", nullable = false)
    private String title;
}
