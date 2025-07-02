package com.example.portfolio_service.repository;

import com.example.portfolio_service.entity.Portfolio;
import com.example.portfolio_service.entity.PortfolioCash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PortfolioCashRepository extends JpaRepository<PortfolioCash, Long> {
    List<PortfolioCash> findAllByPortfolio(Portfolio portfolio);
}
