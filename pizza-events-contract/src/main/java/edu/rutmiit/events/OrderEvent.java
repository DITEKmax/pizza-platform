package edu.rutmiit.events;

import java.math.BigDecimal;

public sealed interface OrderEvent {

    record Created(
            Long orderId,
            Long customerId,
            BigDecimal totalPrice,
            int itemsCount
    ) implements OrderEvent {}

    record Cancelled(
            Long orderId,
            Long customerId,
            String previousStatus
    ) implements OrderEvent {}
}