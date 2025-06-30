package com.example.order_service.service.proto;

import com.portfolio.grpc.PortfolioGrpc;
import com.portfolio.grpc.PortfolioServiceProto;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

@Service
public class PortfolioServiceClient {

    @GrpcClient("portfolio-service")
    private PortfolioGrpc.PortfolioBlockingStub portfolioBlockingStub;

    public PortfolioServiceProto.PortfolioResponse isPortfolioValid(Long userId, Long portfolioId) {
        PortfolioServiceProto.OrderRequest request = PortfolioServiceProto.OrderRequest
                .newBuilder()
                .setUserId(userId)
                .setPortfolioId(portfolioId)
                .build();

        return portfolioBlockingStub.isValidPortfolio(request);
    }
}
