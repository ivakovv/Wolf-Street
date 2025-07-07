package com.example.order_service.service.proto;

import com.aws.protobuf.PortfolioGrpc;
import com.aws.protobuf.PortfolioServiceProto;
import com.example.order_service.dto.CreateRequestDto;
import com.example.order_service.enums.OrderType;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class PortfolioServiceClient {

    @GrpcClient("portfolio-service")
    private PortfolioGrpc.PortfolioBlockingStub portfolioBlockingStub;

    public PortfolioServiceProto.PortfolioResponse isPortfolioValid(Long user_id, CreateRequestDto createRequest) {
        if (createRequest.type() == OrderType.BUY){
            PortfolioServiceProto.OrderBuyRequest request = PortfolioServiceProto.OrderBuyRequest
                    .newBuilder()
                    .setUserId(user_id)
                    .setPortfolioId(createRequest.portfolioId())
                    .setTotal(BigDecimal.valueOf(createRequest.count())
                            .multiply(createRequest.lotPrice())
                            .toPlainString())
                    .build();
            return portfolioBlockingStub.isValidPortfolioForBuy(request);
        }
        else {
            PortfolioServiceProto.OrderSaleRequest request = PortfolioServiceProto.OrderSaleRequest
                    .newBuilder()
                    .setUserId(user_id)
                    .setPortfolioId(createRequest.portfolioId())
                    .setInstrumentId(createRequest.instrumentId())
                    .setCount(createRequest.count())
                    .build();
            return portfolioBlockingStub.isValidPortfolioForSale(request);
        }

    }
}
