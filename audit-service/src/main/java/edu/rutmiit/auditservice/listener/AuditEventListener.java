package edu.rutmiit.auditservice.listener;

import edu.rutmiit.auditservice.model.AuditEntry;
import edu.rutmiit.auditservice.storage.AuditStorage;
import edu.rutmiit.events.EventMetadata;
import edu.rutmiit.events.MenuItemEvent;
import edu.rutmiit.events.OrderEvent;
import edu.rutmiit.events.RoutingKeys;
import org.springframework.amqp.core.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.time.Instant;

@Component
public class AuditEventListener {

    private static final Logger log = LoggerFactory.getLogger(AuditEventListener.class);

    private final AuditStorage auditStorage;
    private final JsonMapper jsonMapper;

    public AuditEventListener(AuditStorage auditStorage, JsonMapper jsonMapper) {
        this.auditStorage = auditStorage;
        this.jsonMapper = jsonMapper;
    }

    @RabbitListener(queues = "q.audit.events", messageConverter = "")
    public void handleEvent(Message message) {
        try {
            byte[] body = message.getBody();
            JsonNode root = jsonMapper.readTree(body);

            JsonNode metaNode = root.get("metadata");
            EventMetadata metadata = jsonMapper.treeToValue(metaNode, EventMetadata.class);

            if (auditStorage.isDuplicate(metadata.eventId())) {
                log.warn("Дубликат события пропущен: eventId={}", metadata.eventId());
                return;
            }

            JsonNode payloadNode = root.get("payload");
            String description = buildDescription(metadata.eventType(), payloadNode);

            AuditEntry entry = auditStorage.save(new AuditEntry(
                    0,
                    metadata.eventId(),
                    metadata.eventType(),
                    metadata.source(),
                    metadata.timestamp(),
                    Instant.now(),
                    description
            ));

            log.info("[AUDIT #{}] {} | {}", entry.sequenceNumber(), metadata.eventType(), description);

        } catch (Exception e) {
            log.error("Ошибка обработки события: {}", e.getMessage(), e);
            throw new RuntimeException("Не удалось обработать событие", e);
        }
    }

    private String buildDescription(String eventType, JsonNode payloadNode) {
        return switch (eventType) {
            case RoutingKeys.ORDER_CREATED -> {
                OrderEvent.Created e = jsonMapper.treeToValue(payloadNode, OrderEvent.Created.class);
                yield String.format("Создан заказ #%d для клиента %d на сумму %s ₽ (позиций: %d)",
                        e.orderId(), e.customerId(), e.totalPrice(), e.itemsCount());
            }
            case RoutingKeys.ORDER_CANCELLED -> {
                OrderEvent.Cancelled e = jsonMapper.treeToValue(payloadNode, OrderEvent.Cancelled.class);
                yield String.format("Отменён заказ #%d клиента %d (был в статусе: %s)",
                        e.orderId(), e.customerId(), e.previousStatus());
            }
            case RoutingKeys.MENU_ITEM_CREATED -> {
                MenuItemEvent.Created e = jsonMapper.treeToValue(payloadNode, MenuItemEvent.Created.class);
                yield String.format("Создана пицца «%s» (id=%d, цена: %s ₽, доступна: %s)",
                        e.name(), e.menuItemId(), e.price(), e.available());
            }
            case RoutingKeys.MENU_ITEM_UPDATED -> {
                MenuItemEvent.Updated e = jsonMapper.treeToValue(payloadNode, MenuItemEvent.Updated.class);
                yield String.format("Обновлена пицца #%d «%s» (цена: %s ₽, доступна: %s)",
                        e.menuItemId(), e.name(), e.price(), e.available());
            }
            case RoutingKeys.MENU_ITEM_DELETED -> {
                MenuItemEvent.Deleted e = jsonMapper.treeToValue(payloadNode, MenuItemEvent.Deleted.class);
                yield String.format("Удалена пицца #%d «%s»",
                        e.menuItemId(), e.name());
            }
            default -> "Неизвестное событие: " + eventType;
        };
    }
}
