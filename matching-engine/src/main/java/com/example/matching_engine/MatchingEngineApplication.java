package com.example.matching_engine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class MatchingEngineApplication {

    public static void main(String[] args) {
        SpringApplication.run(MatchingEngineApplication.class, args);
    }

}
