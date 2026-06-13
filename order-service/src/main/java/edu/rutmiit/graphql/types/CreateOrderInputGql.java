package edu.rutmiit.graphql.types;

import java.util.List;

public record CreateOrderInputGql(
        String customerId,
        String deliveryAddress,
        List<OrderItemInputGql> items,
        String phoneNumber
) {
}
