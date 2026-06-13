package edu.rutmiit.events;

import java.math.BigDecimal;

public sealed interface MenuItemEvent {

    record Created(
            Long menuItemId,
            String name,
            String description,
            BigDecimal price,
            Boolean available
    ) implements MenuItemEvent {}

    record Updated(
            Long menuItemId,
            String name,
            String description,
            BigDecimal price,
            Boolean available
    ) implements MenuItemEvent {}

    record Deleted(
            Long menuItemId,
            String name
    ) implements MenuItemEvent {}
}