package com.example.market_data_service.service.proto;

import com.aws.protobuf.MarketDataServiceGrpc;
import com.aws.protobuf.MarketDataServiceProto;
import com.example.market_data_service.service.interfaces.OrderBookService;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class MarketDataGrpcService extends MarketDataServiceGrpc.MarketDataServiceImplBase {
    private final OrderBookService orderBookService;

    @Override
    public void getPortfolioValue(MarketDataServiceProto.PortfolioValueRequest request, StreamObserver<MarketDataServiceProto.PortfolioValueResponse> responseObserver) {
        log.info("Getting prices for {} instruments", request.getInstrumentIdsCount());
        Map<Long, String> instrumentPrices = getInstrumentPrices(request.getInstrumentIdsList());
        MarketDataServiceProto.PortfolioValueResponse portfolioValueResponse =
                MarketDataServiceProto.PortfolioValueResponse.newBuilder()
                        .putAllInstrumentsPrice(instrumentPrices)
                        .build();
        responseObserver.onNext(portfolioValueResponse);
        responseObserver.onCompleted();
    }

    private Map<Long, String> getInstrumentPrices(List<Long> instruments) {
        return instruments.stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        instrumentId -> {
                            BigDecimal price = orderBookService.getSpread(instrumentId).midPrice();
                            return price == null ? BigDecimal.ZERO.toString() : price.toString();
                        }));
    }
}
