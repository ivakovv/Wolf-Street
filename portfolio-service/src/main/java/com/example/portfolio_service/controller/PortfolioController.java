package com.example.portfolio_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wolfstreet.security_lib.details.JwtDetails;

@RestController
@RequestMapping("/portfolio")
public class PortfolioController {
    
    @GetMapping("/test")
    @PreAuthorize("hasRole('TRADER')")
    public ResponseEntity<String> test(Authentication authentication){
        JwtDetails jwtDetails = (JwtDetails)authentication.getPrincipal();
        return ResponseEntity.ok(jwtDetails.getUserId().toString());
    }
    
    @GetMapping("/open")
    public ResponseEntity<String> open(){
        return ResponseEntity.ok("OPENED");
    }
}
