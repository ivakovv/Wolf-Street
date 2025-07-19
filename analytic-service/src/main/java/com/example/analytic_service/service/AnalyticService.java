package com.example.analytic_service.service;

import com.aws.protobuf.DealMessages;
import com.example.analytic_service.entity.ExecutedDeal;
import com.example.analytic_service.mapper.MapperToExecutedDeal;
import com.example.analytic_service.repository.ExecutedDealRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

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
    public Map<Long, BigDecimal> getInstrumentProfitability(List<Long> instrumentIds, String period) {
        if (instrumentIds == null || instrumentIds.isEmpty()) {
            throw new IllegalArgumentException("Instrument IDs list cannot be empty");
        }

        OffsetDateTime fromDate;
        switch (period) {
            case "1d":
                fromDate = OffsetDateTime.now().minusDays(1);
                break;
            case "1w":
                fromDate = OffsetDateTime.now().minusWeeks(1);
                break;
            case "1m":
                fromDate = OffsetDateTime.now().minusMonths(1);
                break;
            default:
                throw new IllegalArgumentException("Invalid period: " + period);
        }
        String formattedFromDate = fromDate.truncatedTo(java.time.temporal.ChronoUnit.MICROS)
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS"));

        return executedDealRepository.getWeightedProfitability(instrumentIds, formattedFromDate);
    }
}
