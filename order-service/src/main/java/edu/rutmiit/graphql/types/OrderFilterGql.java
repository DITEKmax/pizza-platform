package edu.rutmiit.graphql.types;

import edu.rutmiit.dto.enums.OrderStatus;

public record OrderFilterGql(
        String customerId,
        OrderStatus status
) {
}
