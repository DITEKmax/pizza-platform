package edu.rutmiit.grpckitchenanalytics.service;

import edu.rutmiit.grpc.AnalyzeOrderRequest;
import edu.rutmiit.grpc.OrderAnalysisResponse;
import edu.rutmiit.grpc.OrderAnalyticsGrpc;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class OrderAnalyticsServiceImpl extends OrderAnalyticsGrpc.OrderAnalyticsImplBase {

    private static final Logger log = LoggerFactory.getLogger(OrderAnalyticsServiceImpl.class);

    @Override
    public void analyzeOrder(AnalyzeOrderRequest request, StreamObserver<OrderAnalysisResponse> responseObserver) {

        log.info(
                "gRPC запрос: анализ заказа id={}, customerId={}, сумма={}, позиций={}, пицц={}",
                request.getOrderId(),
                request.getCustomerId(),
                request.getTotalPrice(),
                request.getItemsCount(),
                request.getTotalQuantity()
        );

        int cookingMinutes = estimateCookingMinutes(
                request.getItemsCount(),
                request.getTotalQuantity(),
                request.getTotalPrice()
        );

        String loadLevel = classifyKitchenLoad(
                request.getItemsCount(),
                request.getTotalQuantity(),
                request.getTotalPrice()
        );

        String priority = classifyPriority(
                request.getTotalQuantity(),
                request.getTotalPrice()
        );

        double packagingScore = calculatePackagingComplexity(
                request.getItemsCount(),
                request.getTotalQuantity(),
                request.getTotalPrice()
        );

        String recommendation = buildRecommendation(loadLevel, priority);

        OrderAnalysisResponse response = OrderAnalysisResponse.newBuilder()
                .setOrderId(request.getOrderId())
                .setEstimatedCookingMinutes(cookingMinutes)
                .setKitchenLoadLevel(loadLevel)
                .setPriorityLevel(priority)
                .setPackagingComplexityScore(packagingScore)
                .setRecommendation(recommendation)
                .build();

        log.info(
                "gRPC ответ: orderId={}, готовка={}мин, загрузка={}, приоритет={}, упаковка={}/10",
                response.getOrderId(),
                response.getEstimatedCookingMinutes(),
                response.getKitchenLoadLevel(),
                response.getPriorityLevel(),
                response.getPackagingComplexityScore()
        );

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    private int estimateCookingMinutes(int itemsCount, int totalQuantity, double totalPrice) {
        int minutes = 6 + totalQuantity * 8 + itemsCount * 2;

        if (totalQuantity >= 5) {
            minutes += 10;
        }

        if (totalPrice >= 3000) {
            minutes += 5;
        }

        return minutes;
    }

    private String classifyKitchenLoad(int itemsCount, int totalQuantity, double totalPrice) {
        if (totalQuantity >= 6 || itemsCount >= 5 || totalPrice >= 3500) {
            return "HIGH";
        }

        if (totalQuantity >= 3 || itemsCount >= 3 || totalPrice >= 1800) {
            return "MEDIUM";
        }

        return "LOW";
    }

    private String classifyPriority(int totalQuantity, double totalPrice) {
        if (totalQuantity >= 6) {
            return "BULK_ORDER";
        }

        if (totalPrice >= 2500) {
            return "VIP_FAST_TRACK";
        }

        return "NORMAL";
    }

    private double calculatePackagingComplexity(int itemsCount, int totalQuantity, double totalPrice) {
        double score = 2.0
                + totalQuantity * 1.1
                + itemsCount * 0.7
                + totalPrice / 1500.0;

        double limited = Math.min(score, 10.0);
        return Math.round(limited * 10.0) / 10.0;
    }

    private String buildRecommendation(String loadLevel, String priority) {
        if ("BULK_ORDER".equals(priority)) {
            return "Собрать большой заказ отдельной партией и заранее подготовить коробки";
        }

        if ("VIP_FAST_TRACK".equals(priority)) {
            return "Отдать заказ на приоритетную сборку и проверить комплектацию";
        }

        if ("HIGH".equals(loadLevel)) {
            return "Проверить загрузку печи и не принимать слишком много крупных заказов одновременно";
        }

        return "Готовить в обычной очереди";
    }
}