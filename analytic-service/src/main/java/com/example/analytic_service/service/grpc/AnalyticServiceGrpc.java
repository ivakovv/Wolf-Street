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
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class AnalyticServiceGrpc extends com.aws.protobuf.AnalyticServiceGrpc.AnalyticServiceImplBase {

    private final ExecutedDealRepository executedDealRepository;

    public void getPortfolioHistory(AnalyticServiceProto.PortfolioHistoryRequest request,
                                    StreamObserver<AnalyticServiceProto.PortfolioHistoryResponse> responseObserver) {

        List<ExecutedDeal> deals = executedDealRepository.getDeals(
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

}
