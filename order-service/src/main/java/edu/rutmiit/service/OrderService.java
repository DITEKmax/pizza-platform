package edu.rutmiit.service;

import edu.rutmiit.dto.enums.OrderStatus;
import edu.rutmiit.dto.request.CreateOrderRequest;
import edu.rutmiit.dto.response.MenuItemResponse;
import edu.rutmiit.dto.response.OrderResponse;
import edu.rutmiit.dto.response.PagedResponse;
import edu.rutmiit.event.OrderEventPublisher;
import edu.rutmiit.exception.OrderNotCancellableException;
import edu.rutmiit.exception.ResourceNotFoundException;
import edu.rutmiit.storage.InMemoryStorage;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class OrderService {

    private final InMemoryStorage storage;
    private final MenuService menuService;

    private final OrderEventPublisher orderEventPublisher;

    public OrderService(InMemoryStorage storage, MenuService menuService, OrderEventPublisher orderEventPublisher) {
        this.storage = storage;
        this.menuService= menuService;
        this.orderEventPublisher = orderEventPublisher;
    }

    public OrderResponse findOrderById(Long id){
        return Optional.ofNullable(storage.order.get(id))
                .orElseThrow(() -> new ResourceNotFoundException("Order", id));
    }

    public PagedResponse<OrderResponse> findAllOrders(Long customerId, OrderStatus status, int page, int size) {

        Stream<OrderResponse> stream = storage.order.values().stream()
                .sorted((a, b) -> a.getId().compareTo(b.getId()));

        if (customerId != null){
            stream = stream.filter(b -> b.getCustomerId() != null && b.getCustomerId().equals(customerId));
        }

        if (status != null){
            stream = stream.filter(b -> b.getStatus() != null && b.getStatus().equals(status));
        }

        List<OrderResponse> allOrders = stream.toList();
        int totalElements = allOrders.size();
        int totalPages = size > 0 ? (int) Math.ceil((double) totalElements / size) : 1;
        int from = page * size;
        int to = Math.min(from + size, totalElements);
        List<OrderResponse> content = (from >= totalElements) ? List.of() : allOrders.subList(from, to);
        return new PagedResponse<>(content, page, size, totalElements, totalPages, page >= totalPages - 1);
    }

    public OrderResponse createOrder (CreateOrderRequest request){
        List<OrderResponse.OrderItemResponse> items = new ArrayList<>();

        for (CreateOrderRequest.OrderItemRequest reqItem : request.items()) {
            MenuItemResponse pizza = menuService.findMenuItemById(reqItem.pizzaId());

            OrderResponse.OrderItemResponse orderItem = OrderResponse.OrderItemResponse.builder()
                    .pizzaId(pizza.getId())
                    .pizzaName(pizza.getName())
                    .quantity(reqItem.quantity())
                    .unitPrice(pizza.getPrice())
                    .build();

            items.add(orderItem);
        }

        BigDecimal totalPrice = BigDecimal.ZERO;
        for (OrderResponse.OrderItemResponse item : items) {
            BigDecimal lineSum = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            totalPrice = totalPrice.add(lineSum);
        }

        long id = storage.orderSequence.incrementAndGet();

        OrderResponse order = OrderResponse.builder()
                .id(id)
                .customerId(request.customerId())
                .status(OrderStatus.CREATED)
                .deliveryAddress(request.deliveryAddress())
                .items(items)
                .totalPrice(totalPrice)
                .createdAt(Instant.now())
                .phoneNumber(request.phoneNumber())
                .build();
        storage.order.put(id, order);
        orderEventPublisher.publishCreated(order);
        return order;
    }

    public OrderResponse cancelOrder(Long id){
        OrderResponse existing = findOrderById(id);
        checkOrderOfPossibleToCancel(existing, id);

        OrderResponse cancelled = OrderResponse.builder()
                .id(existing.getId())
                .customerId(existing.getCustomerId())
                .status(OrderStatus.CANCELLED)
                .deliveryAddress(existing.getDeliveryAddress())
                .items(existing.getItems())
                .totalPrice(existing.getTotalPrice())
                .createdAt(existing.getCreatedAt())
                .updatedAt(Instant.now())
                .phoneNumber(existing.getPhoneNumber())
                .build();
        storage.order.put(id, cancelled);
        orderEventPublisher.publishCancelled(cancelled, existing.getStatus().toString());
        return cancelled;
    }


    private void checkOrderOfPossibleToCancel(OrderResponse response, Long id){
        OrderStatus status = response.getStatus();
        if (status == OrderStatus.DELIVERING || status == OrderStatus.DELIVERED
                || status == OrderStatus.CANCELLED) {
            throw new OrderNotCancellableException(id, status.toString());
        }
    }
}
