package com.example.analytic_service.service;

import com.aws.protobuf.DealMessages;
import com.example.analytic_service.entity.ExecutedDeal;
import com.example.analytic_service.mapper.MapperToExecutedDeal;
import com.example.analytic_service.repository.ExecutedDealRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AnalyticService {
    private final ExecutedDealRepository executedDealRepository;

    private final MapperToExecutedDeal mapperToExecutedDeal;

    @Transactional
    public void saveDeals(DealMessages.DealExecutedEvent executedEvent) {
        ExecutedDeal buyExecuteDeal = mapperToExecutedDeal.mapToBuyExecutedDeal(executedEvent);
        ExecutedDeal saleExecuteDeal = mapperToExecutedDeal.mapToSaleExecutedDeal(executedEvent);

        executedDealRepository.save(buyExecuteDeal);
        executedDealRepository.save(saleExecuteDeal);

    }
}
