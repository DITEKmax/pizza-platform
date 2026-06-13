package edu.rutmiit.events;

public final class RoutingKeys {

    private RoutingKeys() {
    }
    public static final String EXCHANGE = "pizza.events";

    public static final String MENU_ITEM_CREATED = "menuitem.created";
    public static final String MENU_ITEM_UPDATED = "menuitem.updated";
    public static final String MENU_ITEM_DELETED = "menuitem.deleted";

    public static final String ORDER_ENRICHED = "order.enriched";
    public static final String ORDER_CREATED = "order.created";
    public static final String ORDER_CANCELLED = "order.cancelled";

    public static final String ALL_MENU_ITEM_EVENTS = "menuitem.*";
    public static final String ALL_ORDER_EVENTS = "order.*";
    public static final String ALL_EVENTS = "#";
}