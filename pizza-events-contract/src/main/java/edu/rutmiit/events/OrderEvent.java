package edu.rutmiit.events;

import java.math.BigDecimal;

public sealed interface OrderEvent {

    record Created(
            Long orderId,
            Long customerId,
            BigDecimal totalPrice,
            int itemsCount,
            Integer totalQuantity
    ) implements OrderEvent {}

    record Cancelled(
            Long orderId,
            Long customerId,
            String previousStatus
    ) implements OrderEvent {}

    record Enriched(
            Long orderId,
            Long customerId,
            BigDecimal totalPrice,
            int itemsCount,
            Integer totalQuantity,
            int estimatedCookingMinutes,
            String kitchenLoadLevel,
            String priorityLevel,
            double packagingComplexityScore,
            String recommendation
    ) implements OrderEvent {}
}