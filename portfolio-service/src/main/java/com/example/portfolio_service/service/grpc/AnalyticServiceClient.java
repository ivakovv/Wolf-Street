package com.example.portfolio_service.service.grpc;

import com.aws.protobuf.AnalyticServiceGrpc;
import com.aws.protobuf.AnalyticServiceProto;
import com.example.portfolio_service.dto.profitability.PortfolioProfitabilityRequest;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AnalyticServiceClient {

    @GrpcClient("analytic-service")
    private AnalyticServiceGrpc.AnalyticServiceBlockingStub analyticServiceBlockingStub;

    public AnalyticServiceProto.PortfolioHistoryResponse getPortfolioHistory(Long portfolioId, Long from, Long to){
        log.info("Getting history for portfolio: {}", portfolioId);
        return analyticServiceBlockingStub.getPortfolioHistory(
                AnalyticServiceProto.PortfolioHistoryRequest.newBuilder()
                        .setPortfolioId(portfolioId)
                        .setLowerLimit(from)
                        .setHigherLimit(to)
                        .build());
    }

    public AnalyticServiceProto.PortfolioProfitabilityResponse getPortfolioProfitability(Long portfolioId, PortfolioProfitabilityRequest request){
        log.info("Getting portfolio profitability for portfolio: {}", portfolioId);
        return analyticServiceBlockingStub.getPortfolioProfitability(
                AnalyticServiceProto.PortfolioInstrumentsRequest.newBuilder()
                        .setPortfolioId(portfolioId)
                        .addAllInstrumentsId(request.instrumentIds())
                        .build());
    }
}
