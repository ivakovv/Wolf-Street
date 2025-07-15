package com.example.analytic_service.service;

import com.aws.protobuf.DealMessages;
import com.example.analytic_service.entity.ExecutedDeal;
import com.example.analytic_service.mapper.MapperToExecutedDeal;
import com.example.analytic_service.repository.ExecutedDealRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
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

        String dateCondition;
        switch (period) {
            case "1 day":
                dateCondition = "created_at >= subtractDays(now(), 1)";
                break;
            case "1 week":
                dateCondition = "created_at >= subtractWeeks(now(), 1)";
                break;
            case "1 month":
                dateCondition = "created_at >= subtractMonths(now(), 1)";
                break;
            default:
                throw new IllegalArgumentException("Invalid period: " + period);
        }

        Map<Long, BigDecimal> buyMap = executedDealRepository.getLastBuyPrices(instrumentIds, dateCondition);
        Map<Long, BigDecimal> saleMap = executedDealRepository.getLastSalePrices(instrumentIds, dateCondition);

        Map<Long, BigDecimal> result = new HashMap<>();
        for (Long id : instrumentIds) {
            BigDecimal buy = buyMap.get(id);
            BigDecimal sale = saleMap.get(id);
            if (buy != null && sale != null && buy.compareTo(BigDecimal.ZERO) != 0) {
                BigDecimal profit = sale.subtract(buy)
                        .divide(buy, 6, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100))
                        .setScale(2, RoundingMode.HALF_UP);
                result.put(id, profit);
            } else {
                result.put(id, BigDecimal.ZERO);
            }
        }
        return result;
    }
}
