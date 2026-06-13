package edu.rutmiit.grpcorderenrichment.publisher;

import edu.rutmiit.events.EventEnvelope;
import edu.rutmiit.events.OrderEvent;
import edu.rutmiit.events.RoutingKeys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class EnrichmentEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(EnrichmentEventPublisher.class);
    private static final String SOURCE = "grpc-order-enrichment-client";

    private final RabbitTemplate rabbitTemplate;

    public EnrichmentEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishEnriched(OrderEvent.Enriched enrichedEvent) {
        try {
            EventEnvelope<OrderEvent> envelope = EventEnvelope.wrap(
                    enrichedEvent,
                    SOURCE,
                    RoutingKeys.ORDER_ENRICHED
            );

            rabbitTemplate.convertAndSend(
                    RoutingKeys.EXCHANGE,
                    RoutingKeys.ORDER_ENRICHED,
                    envelope
            );

            log.info(
                    "Событие отправлено: {} [orderId={}, eventId={}]",
                    RoutingKeys.ORDER_ENRICHED,
                    enrichedEvent.orderId(),
                    envelope.metadata().eventId()
            );

        } catch (Exception e) {
            log.error(
                    "Не удалось отправить событие {}: {}",
                    RoutingKeys.ORDER_ENRICHED,
                    e.getMessage()
            );
        }
    }
}