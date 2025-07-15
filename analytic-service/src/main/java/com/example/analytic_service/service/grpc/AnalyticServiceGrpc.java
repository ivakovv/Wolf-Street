package com.example.analytic_service.service.grpc;

import com.aws.protobuf.AnalyticServiceProto;
import com.example.analytic_service.entity.ExecutedDeal;
import com.example.analytic_service.enums.OrderType;
import com.example.analytic_service.repository.ExecutedDealRepository;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import com.google.protobuf.Timestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class AnalyticServiceGrpc extends com.aws.protobuf.AnalyticServiceGrpc.AnalyticServiceImplBase {

    private final ExecutedDealRepository executedDealRepository;

    @Override
    public void getPortfolioHistory(AnalyticServiceProto.PortfolioHistoryRequest request,
                                    StreamObserver<AnalyticServiceProto.PortfolioHistoryResponse> responseObserver) {

        List<ExecutedDeal> deals = executedDealRepository.getDeals(
                request.getPortfolioId(),
                request.getLowerLimit(),
                request.getHigherLimit());

        log.info("Retrieved {} deals for portfolioId: {}, offset: {}, limit: {}",
                deals.size(),
                request.getPortfolioId(),
                request.getLowerLimit(),
                request.getHigherLimit());

        List<AnalyticServiceProto.Deal> protoDeals = deals.stream()
                .map(this::convertToProtoDeal)
                .collect(Collectors.toList());

        AnalyticServiceProto.PortfolioHistoryResponse response = AnalyticServiceProto.PortfolioHistoryResponse.newBuilder()
                .addAllDeals(protoDeals)
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();

        log.info("Portfolio history request completed successfully for portfolioId: {}",
                request.getPortfolioId());
    }

    private AnalyticServiceProto.Deal convertToProtoDeal(ExecutedDeal deal) {
        Instant instant = deal.getCreatedAt().toInstant();
        Timestamp timestamp = Timestamp.newBuilder()
                .setSeconds(instant.getEpochSecond())
                .setNanos(instant.getNano())
                .build();

        return AnalyticServiceProto.Deal.newBuilder()
                .setDealId(deal.getOrderId())
                .setDealType(deal.getOrderType() == OrderType.BUY ?
                        AnalyticServiceProto.Deal.DealType.BUY :
                        AnalyticServiceProto.Deal.DealType.SALE)
                .setTimestamp(timestamp)
                .setInstrumentId(deal.getInstrumentId())
                .setCount(deal.getCount())
                .setLotPrice(deal.getLotPrice().toString())
                .build();
    }
    @Override
    public void getPortfolioProfitability(AnalyticServiceProto.PortfolioInstrumentsRequest request,
                                          StreamObserver<AnalyticServiceProto.PortfolioProfitabilityResponse> responseObserver) {
        try {
            log.info("Received portfolio profitability request for portfolioId: {}, instruments: {}",
                    request.getPortfolioId(), request.getInstrumentsIdList());

            Map<Long, BigDecimal> profitabilityMap = executedDealRepository.calculateRealizedProfit(
                    request.getPortfolioId(),
                    request.getInstrumentsIdList());

            AnalyticServiceProto.PortfolioProfitabilityResponse.Builder responseBuilder =
                    AnalyticServiceProto.PortfolioProfitabilityResponse.newBuilder()
                            .setPortfolioId(request.getPortfolioId());

            profitabilityMap.forEach((instrumentId, profit) ->
                    responseBuilder.putMapProfitability(instrumentId, profit.toString()));

            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();

            log.info("Portfolio profitability calculation completed for portfolioId: {}", request.getPortfolioId());
        } catch (Exception e) {
            log.error("Error calculating portfolio profitability", e);
            responseObserver.onError(e);
        }
    }
}
