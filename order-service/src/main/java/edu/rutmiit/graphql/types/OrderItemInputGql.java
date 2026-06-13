package edu.rutmiit.graphql.types;

public record OrderItemInputGql(
        String pizzaId,
        Integer quantity
) {
}
