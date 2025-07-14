package com.example.portfolio_service.service.grpc;

import com.aws.protobuf.MarketDataServiceGrpc;
import com.aws.protobuf.MarketDataServiceProto;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class MarketDataServiceClient {
    @GrpcClient("market-data-service")
    private MarketDataServiceGrpc.MarketDataServiceBlockingStub marketDataServiceBlockingStub;

    public MarketDataServiceProto.PortfolioValueResponse getPortfolioValue(List<Long> instrumentIds){
        log.info("Getting portfolio value!");
        return marketDataServiceBlockingStub.getPortfolioValue(
                MarketDataServiceProto.PortfolioValueRequest.newBuilder()
                        .addAllInstrumentIds(instrumentIds)
                        .build()
        );
    }
}
