package com.example.portfolio_service.service.grpc;

import com.aws.protobuf.AnalyticServiceGrpc;
import com.aws.protobuf.AnalyticServiceProto;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AnalyticServiceClient {
    @GrpcClient("analytic-service")
    private AnalyticServiceGrpc.AnalyticServiceBlockingStub analyticServiceBlockingStub;

    public AnalyticServiceProto.PortfolioHistoryResponse getPortfolioHistory(Long portfolioId){
        log.info("Getting history for portfolio: {}", portfolioId);
        return analyticServiceBlockingStub.getPortfolioHistory(
                AnalyticServiceProto.PortfolioHistoryRequest.newBuilder()
                        .setPortfolioId(portfolioId)
                        .build());
    }
}
