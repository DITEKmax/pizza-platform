package edu.rutmiit.controllers;

import edu.rutmiit.assemblers.OrderModelAssembler;
import edu.rutmiit.dto.enums.OrderStatus;
import edu.rutmiit.dto.request.CreateOrderRequest;
import edu.rutmiit.dto.response.OrderResponse;
import edu.rutmiit.dto.response.PagedResponse;
import edu.rutmiit.endpoints.OrderApi;
import edu.rutmiit.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderController implements OrderApi {

    private final OrderService orderService;
    private final OrderModelAssembler orderModelAssembler;
    private final PagedResourcesAssembler<OrderResponse> pagedAssembler;


    public OrderController(OrderService orderService, OrderModelAssembler orderModelAssembler, PagedResourcesAssembler<OrderResponse> pagedAssembler) {
        this.orderService = orderService;
        this.orderModelAssembler = orderModelAssembler;
        this.pagedAssembler = pagedAssembler;
    }

    @Override
    public EntityModel<OrderResponse> getOrderById(Long id) {
        return orderModelAssembler.toModel(orderService.findOrderById(id));
    }

    @Override
    public PagedModel<EntityModel<OrderResponse>> getAllOrders(Long customerId, OrderStatus status, int page, int size) {
        PagedResponse<OrderResponse> paged = orderService.findAllOrders(customerId, status, page, size);
        Page<OrderResponse> springPage = new PageImpl<>(
                paged.content(),
                PageRequest.of(paged.pageNumber(), paged.pageSize()),
                paged.totalElements()

        );

        return pagedAssembler.toModel(springPage, orderModelAssembler);

    }

    @Override
    public ResponseEntity<EntityModel<OrderResponse>> createOrder(CreateOrderRequest request) {
        OrderResponse create = orderService.createOrder(request);
        EntityModel<OrderResponse> model = orderModelAssembler.toModel(create);
        return ResponseEntity.created(model.getRequiredLink("self").toUri())
                .body(model);
    }

    @Override
    public EntityModel<OrderResponse> cancelOrder(Long id) {
        return orderModelAssembler.toModel(orderService.cancelOrder(id));
    }
}
