package com.example.portfolio_service.service.grpc;

import com.aws.protobuf.PortfolioGrpc;
import com.example.portfolio_service.service.interfaces.PortfolioValidationService;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
@RequiredArgsConstructor
public class PortfolioGrpcService extends PortfolioGrpc.PortfolioImplBase {
    private final PortfolioValidationService portfolioValidationService;
}
