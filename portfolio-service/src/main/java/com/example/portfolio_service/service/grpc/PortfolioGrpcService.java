package com.example.portfolio_service.service.grpc;

import com.aws.protobuf.PortfolioGrpc;
import com.aws.protobuf.PortfolioServiceProto;
import com.example.portfolio_service.service.interfaces.PortfolioValidationService;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class PortfolioGrpcService extends PortfolioGrpc.PortfolioImplBase {
    private final PortfolioValidationService portfolioValidationService;

    @Override
    public void isValidPortfolioForSale(PortfolioServiceProto.OrderSaleRequest request, StreamObserver<PortfolioServiceProto.PortfolioResponse> responseObserver) {
        boolean isValid = portfolioValidationService.validateAndBlockForSale(request.getUserId(), request.getPortfolioId(), request.getInstrumentId(), request.getCount());
        PortfolioServiceProto.PortfolioResponse portfolioResponse = createResponse(
                isValid,
                isValid ? "Продажа возможна!" : "Недостаточно инструмента!");
        log.info("Response for sale sent {}: {}, {}", isValid, portfolioResponse.getIsValid(), portfolioResponse.getDescription());
        responseObserver.onNext(portfolioResponse);
        responseObserver.onCompleted();
    }

    @Override
    public void isValidPortfolioForBuy(PortfolioServiceProto.OrderBuyRequest request, StreamObserver<PortfolioServiceProto.PortfolioResponse> responseObserver) {
        boolean isValid = portfolioValidationService.validateAndBlockForBuy(request.getUserId(), request.getPortfolioId(), request.getTotal());
        PortfolioServiceProto.PortfolioResponse portfolioResponse = createResponse(
                isValid,
                isValid ? "Покупка возможна!" : "Недостаточно средств!");
        log.info("Response for buy sent {}, {}, {}", isValid, portfolioResponse.getIsValid(), portfolioResponse.getDescription());
        responseObserver.onNext(portfolioResponse);
        responseObserver.onCompleted();
    }

    private PortfolioServiceProto.PortfolioResponse createResponse(boolean isValid, String description) {
        return PortfolioServiceProto.PortfolioResponse.newBuilder()
                .setIsValid(isValid)
                .setDescription(description)
                .build();
    }
}
