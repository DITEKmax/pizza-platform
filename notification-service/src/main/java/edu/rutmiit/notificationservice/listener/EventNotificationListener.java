package edu.rutmiit.notificationservice.listener;

import edu.rutmiit.events.EventMetadata;
import edu.rutmiit.events.MenuItemEvent;
import edu.rutmiit.events.OrderEvent;
import edu.rutmiit.events.RoutingKeys;
import edu.rutmiit.notificationservice.config.RabbitMQConfig;
import edu.rutmiit.notificationservice.websocket.NotificationWebSocketHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.time.Instant;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class EventNotificationListener {

    private static final Logger log = LoggerFactory.getLogger(EventNotificationListener.class);

    private final NotificationWebSocketHandler webSocketHandler;
    private final JsonMapper jsonMapper;

    private final Set<String> processedEventIds = ConcurrentHashMap.newKeySet();

    public EventNotificationListener(
            NotificationWebSocketHandler webSocketHandler,
            JsonMapper jsonMapper
    ) {
        this.webSocketHandler = webSocketHandler;
        this.jsonMapper = jsonMapper;
    }

    @RabbitListener(queues = RabbitMQConfig.NOTIFICATIONS_QUEUE, messageConverter = "")
    public void handleEvent(Message message) {
        try {
            JsonNode root = jsonMapper.readTree(message.getBody());

            JsonNode metadataNode = root.get("metadata");
            EventMetadata metadata = jsonMapper.treeToValue(metadataNode, EventMetadata.class);

            if (!processedEventIds.add(metadata.eventId())) {
                log.warn("Дубликат уведомления пропущен: eventId={}", metadata.eventId());
                return;
            }

            JsonNode payloadNode = root.get("payload");

            String title = buildTitle(metadata.eventType());
            String description = buildDescription(metadata.eventType(), payloadNode);
            String icon = resolveIcon(metadata.eventType());
            String level = resolveLevel(metadata.eventType());

            NotificationPayload notification = new NotificationPayload(
                    "NOTIFICATION",
                    metadata.eventId(),
                    metadata.eventType(),
                    title,
                    description,
                    icon,
                    level,
                    metadata.source(),
                    metadata.timestamp().toString(),
                    Instant.now().toString()
            );

            String notificationJson = jsonMapper.writeValueAsString(notification);

            webSocketHandler.broadcast(notificationJson);

            log.info(
                    "[NOTIFY] {} | {} (клиентов: {})",
                    metadata.eventType(),
                    description,
                    webSocketHandler.getActiveConnectionCount()
            );

        } catch (Exception e) {
            log.error("Ошибка обработки события для WebSocket-уведомлений: {}", e.getMessage(), e);
            throw new RuntimeException("Не удалось обработать событие для уведомлений", e);
        }
    }

    private String buildTitle(String eventType) {
        return switch (eventType) {
            case RoutingKeys.MENU_ITEM_CREATED -> "Пицца добавлена в меню";
            case RoutingKeys.MENU_ITEM_UPDATED -> "Пицца обновлена";
            case RoutingKeys.MENU_ITEM_DELETED -> "Пицца удалена";

            case RoutingKeys.ORDER_CREATED -> "Новый заказ";
            case RoutingKeys.ORDER_CANCELLED -> "Заказ отменён";
            case RoutingKeys.ORDER_ENRICHED -> "Кухонная аналитика";

            default -> "Событие: " + eventType;
        };
    }

    private String buildDescription(String eventType, JsonNode payloadNode) {
        try {
            return switch (eventType) {
                case RoutingKeys.MENU_ITEM_CREATED -> {
                    MenuItemEvent.Created e = jsonMapper.treeToValue(payloadNode, MenuItemEvent.Created.class);
                    yield "В меню добавлена пицца «%s» за %s ₽ (доступна: %s)".formatted(
                            e.name(),
                            e.price(),
                            yesNo(e.available())
                    );
                }

                case RoutingKeys.MENU_ITEM_UPDATED -> {
                    MenuItemEvent.Updated e = jsonMapper.treeToValue(payloadNode, MenuItemEvent.Updated.class);
                    yield "Обновлена пицца #%d «%s»: цена %s ₽, доступна: %s".formatted(
                            e.menuItemId(),
                            e.name(),
                            e.price(),
                            yesNo(e.available())
                    );
                }

                case RoutingKeys.MENU_ITEM_DELETED -> {
                    MenuItemEvent.Deleted e = jsonMapper.treeToValue(payloadNode, MenuItemEvent.Deleted.class);
                    yield "Удалена пицца #%d «%s»".formatted(
                            e.menuItemId(),
                            e.name()
                    );
                }

                case RoutingKeys.ORDER_CREATED -> {
                    OrderEvent.Created e = jsonMapper.treeToValue(payloadNode, OrderEvent.Created.class);
                    yield "Создан заказ #%d для клиента %d на сумму %s ₽: позиций %d, пицц %d".formatted(
                            e.orderId(),
                            e.customerId(),
                            e.totalPrice(),
                            e.itemsCount(),
                            e.totalQuantity()
                    );
                }

                case RoutingKeys.ORDER_CANCELLED -> {
                    OrderEvent.Cancelled e = jsonMapper.treeToValue(payloadNode, OrderEvent.Cancelled.class);
                    yield "Отменён заказ #%d клиента %d (предыдущий статус: %s)".formatted(
                            e.orderId(),
                            e.customerId(),
                            e.previousStatus()
                    );
                }

                case RoutingKeys.ORDER_ENRICHED -> {
                    OrderEvent.Enriched e = jsonMapper.treeToValue(payloadNode, OrderEvent.Enriched.class);
                    yield "Заказ #%d: готовка ~%d мин, загрузка кухни: %s, приоритет: %s, упаковка %.1f/10. %s".formatted(
                            e.orderId(),
                            e.estimatedCookingMinutes(),
                            e.kitchenLoadLevel(),
                            e.priorityLevel(),
                            e.packagingComplexityScore(),
                            e.recommendation()
                    );
                }

                default -> "Неизвестное событие: " + eventType;
            };
        } catch (Exception e) {
            return "Событие " + eventType + " получено, но payload не удалось разобрать";
        }
    }

    private String resolveIcon(String eventType) {
        return switch (eventType) {
            case RoutingKeys.MENU_ITEM_CREATED -> "pizza-plus";
            case RoutingKeys.MENU_ITEM_UPDATED -> "pizza-edit";
            case RoutingKeys.MENU_ITEM_DELETED -> "pizza-remove";

            case RoutingKeys.ORDER_CREATED -> "order-new";
            case RoutingKeys.ORDER_CANCELLED -> "order-cancel";
            case RoutingKeys.ORDER_ENRICHED -> "kitchen";

            default -> "bell";
        };
    }

    private String resolveLevel(String eventType) {
        return switch (eventType) {
            case RoutingKeys.MENU_ITEM_DELETED, RoutingKeys.ORDER_CANCELLED -> "warning";
            case RoutingKeys.MENU_ITEM_UPDATED, RoutingKeys.ORDER_ENRICHED -> "info";
            default -> "success";
        };
    }

    private String yesNo(Boolean value) {
        return Boolean.TRUE.equals(value) ? "да" : "нет";
    }

    record NotificationPayload(
            String type,
            String eventId,
            String eventType,
            String title,
            String description,
            String icon,
            String level,
            String source,
            String eventTimestamp,
            String receivedAt
    ) {
    }
}