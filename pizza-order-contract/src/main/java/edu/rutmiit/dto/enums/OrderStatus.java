package edu.rutmiit.dto.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Статус заказа")
public enum OrderStatus {

    @Schema(description = "Заказ создан")
    CREATED,

    @Schema(description = "Кухня готовит заказ")
    COOKING,

    @Schema(description = "Заказ готов, ожидает курьера")
    READY,

    @Schema(description = "Курьер забрал заказ, везёт клиенту")
    DELIVERING,

    @Schema(description = "Заказ доставлен")
    DELIVERED,

    @Schema(description = "Заказ отменён")
    CANCELLED

}