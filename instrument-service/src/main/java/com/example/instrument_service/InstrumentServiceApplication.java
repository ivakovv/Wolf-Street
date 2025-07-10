package com.example.instrument_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = {"com.example.instrument_service", "com.wolfstreet.security_lib"})
@SpringBootApplication
public class InstrumentServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(InstrumentServiceApplication.class, args);
    }

}
