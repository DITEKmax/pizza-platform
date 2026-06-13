package edu.rutmiit.assemblers;

import edu.rutmiit.controllers.OrderController;
import edu.rutmiit.dto.response.OrderResponse;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class OrderModelAssembler implements RepresentationModelAssembler<OrderResponse, EntityModel<OrderResponse>> {
    @Override
    public EntityModel<OrderResponse> toModel(OrderResponse order) {
        return EntityModel.of(order,
                linkTo(methodOn(OrderController.class).getOrderById(order.getId())).withSelfRel(),
                linkTo(methodOn(OrderController.class).getAllOrders(null, null, 0, 20)).withRel("collection")
                );
    }
}
