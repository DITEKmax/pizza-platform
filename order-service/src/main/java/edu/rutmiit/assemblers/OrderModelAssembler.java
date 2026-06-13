package edu.rutmiit.assemblers;

import edu.rutmiit.controllers.OrderController;
import edu.rutmiit.dto.response.OrderResponse;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import edu.rutmiit.dto.enums.OrderStatus;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class OrderModelAssembler implements RepresentationModelAssembler<OrderResponse, EntityModel<OrderResponse>> {

    @Override
    public EntityModel<OrderResponse> toModel(OrderResponse order) {
        EntityModel<OrderResponse> model = EntityModel.of(order,
                linkTo(methodOn(OrderController.class).getOrderById(order.getId())).withSelfRel(),
                linkTo(methodOn(OrderController.class).getAllOrders(null, null, 0, 20)).withRel("collection"));

        OrderStatus status = order.getStatus();
        boolean cancellable = status != OrderStatus.DELIVERING && status != OrderStatus.DELIVERED && status != OrderStatus.CANCELLED;

        if (cancellable) {
            model.add(
                    linkTo(methodOn(OrderController.class).cancelOrder(order.getId())).withRel("cancel"));
        }

        return model;
    }
}
