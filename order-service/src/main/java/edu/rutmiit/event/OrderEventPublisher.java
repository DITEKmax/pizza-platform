package edu.rutmiit.event;

import edu.rutmiit.dto.response.OrderResponse;
import edu.rutmiit.events.EventEnvelope;
import edu.rutmiit.events.OrderEvent;
import edu.rutmiit.events.RoutingKeys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class OrderEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(OrderEventPublisher.class);
    private static final String SOURCE = "order-service";

    private final RabbitTemplate rabbitTemplate;

    public OrderEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishCreated(OrderResponse order){
        var event = new OrderEvent.Created(
                order.getId(),
                order.getCustomerId(),
                order.getTotalPrice(),
                order.getItems().size(),
                order.getTotalQuantity()
        );
        send(RoutingKeys.ORDER_CREATED, event);
    }

    public void publishCancelled(OrderResponse order, String previousStatus){
        var event = new OrderEvent.Cancelled(
                order.getId(),
                order.getCustomerId(),
                previousStatus
        );

        send(RoutingKeys.ORDER_CANCELLED, event);
    }

    private void send(String routingKey, OrderEvent event){
        try {
            EventEnvelope<OrderEvent> envelope = EventEnvelope.wrap(event, SOURCE, routingKey);
            rabbitTemplate.convertAndSend(RoutingKeys.EXCHANGE, routingKey, envelope);
            log.info("Событие отправлено: {} [eventId={}]", routingKey, envelope.metadata().eventId());
        } catch (Exception e){
            log.error("Не удалось отправить событие {}: {}", routingKey, e.getMessage());
        }
    }
}


