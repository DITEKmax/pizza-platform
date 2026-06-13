package edu.rutmiit.event;

import edu.rutmiit.dto.response.MenuItemResponse;
import edu.rutmiit.events.EventEnvelope;
import edu.rutmiit.events.MenuItemEvent;
import edu.rutmiit.events.RoutingKeys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class MenuItemEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(MenuItemEventPublisher.class);
    private static final String SOURCE =  "order-service";

    private final RabbitTemplate rabbitTemplate;

    public MenuItemEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishCreated(MenuItemResponse menuItem){
        var event = new MenuItemEvent.Created(
                menuItem.getId(),
                menuItem.getName(),
                menuItem.getDescription(),
                menuItem.getPrice(),
                menuItem.getAvailable()
        );

        send(RoutingKeys.MENU_ITEM_CREATED, event);
    }

    public void publishUpdated(MenuItemResponse menuItem){
        var event = new MenuItemEvent.Updated(
                menuItem.getId(),
                menuItem.getName(),
                menuItem.getDescription(),
                menuItem.getPrice(),
                menuItem.getAvailable()
        );

        send(RoutingKeys.MENU_ITEM_UPDATED, event);
    }

    public void publishDeleted(MenuItemResponse menuItem){
        var event = new MenuItemEvent.Deleted(
                menuItem.getId(),
                menuItem.getName()
        );
        send(RoutingKeys.MENU_ITEM_DELETED, event);
    }

    private void send(String routingKey, MenuItemEvent event){
        try {
            EventEnvelope<MenuItemEvent> envelope = EventEnvelope.wrap(event, SOURCE, routingKey);
            rabbitTemplate.convertAndSend(RoutingKeys.EXCHANGE, routingKey, envelope);
            log.info("Событие отправлено: {} [eventId={}]", routingKey, envelope.metadata().eventId());
        } catch (Exception e){
            log.error("Не удалось отправить событие {}: {}", routingKey, e.getMessage());
        }
    }
}
