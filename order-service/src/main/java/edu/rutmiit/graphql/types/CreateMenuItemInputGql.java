package edu.rutmiit.graphql.types;

import java.math.BigDecimal;

public record CreateMenuItemInputGql(
        String name,
        String description,
        BigDecimal price,
        Boolean available
) {
}
