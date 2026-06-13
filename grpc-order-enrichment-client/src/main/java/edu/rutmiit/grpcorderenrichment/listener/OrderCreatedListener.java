package edu.rutmiit.grpcorderenrichment.listener;

import edu.rutmiit.events.EventMetadata;
import edu.rutmiit.events.OrderEvent;
import edu.rutmiit.grpc.AnalyzeOrderRequest;
import edu.rutmiit.grpc.OrderAnalysisResponse;
import edu.rutmiit.grpc.OrderAnalyticsGrpc;
import edu.rutmiit.grpcorderenrichment.config.RabbitMQConfig;
import edu.rutmiit.grpcorderenrichment.publisher.EnrichmentEventPublisher;
import io.grpc.StatusRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

@Component
public class OrderCreatedListener {

    private static final Logger log = LoggerFactory.getLogger(OrderCreatedListener.class);

    private final OrderAnalyticsGrpc.OrderAnalyticsBlockingStub analyticsStub;
    private final EnrichmentEventPublisher enrichmentPublisher;
    private final JsonMapper jsonMapper;

    public OrderCreatedListener(OrderAnalyticsGrpc.OrderAnalyticsBlockingStub analyticsStub,
                                EnrichmentEventPublisher enrichmentPublisher,
                                JsonMapper jsonMapper) {
        this.analyticsStub = analyticsStub;
        this.enrichmentPublisher = enrichmentPublisher;
        this.jsonMapper = jsonMapper;
    }

    @RabbitListener(queues = RabbitMQConfig.ENRICHMENT_QUEUE, messageConverter = "")
    public void handleOrderCreated(Message message) {
        try {
            JsonNode root = jsonMapper.readTree(message.getBody());

            EventMetadata metadata = jsonMapper.treeToValue(
                    root.get("metadata"),
                    EventMetadata.class
            );

            OrderEvent.Created orderCreated = jsonMapper.treeToValue(
                    root.get("payload"),
                    OrderEvent.Created.class
            );

            log.info(
                    "Получено событие order.created: orderId={}, customerId={}, сумма={}, позиций={}, пицц={} [eventId={}]",
                    orderCreated.orderId(),
                    orderCreated.customerId(),
                    orderCreated.totalPrice(),
                    orderCreated.itemsCount(),
                    orderCreated.totalQuantity(),
                    metadata.eventId()
            );

            AnalyzeOrderRequest grpcRequest = AnalyzeOrderRequest.newBuilder()
                    .setOrderId(orderCreated.orderId())
                    .setCustomerId(orderCreated.customerId())
                    .setTotalPrice(orderCreated.totalPrice().doubleValue())
                    .setItemsCount(orderCreated.itemsCount())
                    .setTotalQuantity(orderCreated.totalQuantity())
                    .build();

            log.info("Вызов gRPC: OrderAnalytics.AnalyzeOrder(orderId={})", orderCreated.orderId());

            OrderAnalysisResponse grpcResponse = analyticsStub.analyzeOrder(grpcRequest);

            log.info(
                    "gRPC ответ получен: orderId={}, готовка={}мин, загрузка={}, приоритет={}, упаковка={}/10",
                    grpcResponse.getOrderId(),
                    grpcResponse.getEstimatedCookingMinutes(),
                    grpcResponse.getKitchenLoadLevel(),
                    grpcResponse.getPriorityLevel(),
                    grpcResponse.getPackagingComplexityScore()
            );

            OrderEvent.Enriched enriched = new OrderEvent.Enriched(
                    grpcResponse.getOrderId(),
                    orderCreated.customerId(),
                    orderCreated.totalPrice(),
                    orderCreated.itemsCount(),
                    orderCreated.totalQuantity(),
                    grpcResponse.getEstimatedCookingMinutes(),
                    grpcResponse.getKitchenLoadLevel(),
                    grpcResponse.getPriorityLevel(),
                    grpcResponse.getPackagingComplexityScore(),
                    grpcResponse.getRecommendation()
            );

            enrichmentPublisher.publishEnriched(enriched);

            log.info(
                    "Заказ обогащён: orderId={} и order.enriched отправлено",
                    orderCreated.orderId()
            );

        } catch (StatusRuntimeException e) {
            log.error(
                    "gRPC ошибка при обогащении заказа: {} ({})",
                    e.getStatus().getDescription(),
                    e.getStatus().getCode()
            );
            throw new RuntimeException("gRPC-вызов завершился ошибкой", e);

        } catch (Exception e) {
            log.error("Ошибка обработки события order.created: {}", e.getMessage(), e);
            throw new RuntimeException("Не удалось обработать событие order.created", e);
        }
    }
}