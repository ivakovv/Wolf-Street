package com.example.instrument_service.repository;

import com.example.instrument_service.entity.Instrument;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface InstrumentRepository extends JpaRepository<Instrument, Long>{
    Optional<Instrument> findById(Long id);
    Optional<Instrument> findByTitle(String title);
}
