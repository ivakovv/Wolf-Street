package com.example.analytic_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.example.analytic_service", "com.wolfstreet.security_lib"})
public class AnalyticServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AnalyticServiceApplication.class, args);
    }

}
