package edu.rutmiit.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import edu.rutmiit.dto.enums.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Getter
@Builder
@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Relation(collectionRelation = "orders", itemRelation = "order")
@Schema(description = "Информация о заказе")
public class OrderResponse extends RepresentationModel<OrderResponse> {

    @Schema(description = "Уникальный идентификатор заказа", example = "1")
    private final Long id;

    @Schema(description = "ID клиента", example = "42")
    private final Long customerId;

    @Schema(description = "Текущий статус заказа", example = "READY")
    private final OrderStatus status;

    @Schema(description = "Адрес доставки", example = "ул. Пушкина, д. 10, кв. 5")
    private final String deliveryAddress;

    @Schema(description = "Позиции заказа")
    private final List<OrderItemResponse> items;

    @Schema(description = "Общее количество пицц в заказе", example = "3")
    private final Integer totalQuantity;

    @Schema(description = "Итоговая сумма заказа", example = "1290.00")
    private final BigDecimal totalPrice;

    @Schema(description = "Момент создания заказа")
    private final Instant createdAt;

    @Schema(description = "Момент последнего обновления статуса")
    private final Instant updatedAt;

    @Schema(description = "Номер телефона")
    private final String phoneNumber;

    @Getter
    @Builder
    @Schema(description = "Позиция заказа")
    public static class OrderItemResponse {

        @Schema(description = "ID пиццы", example = "7")
        private final Long pizzaId;

        @Schema(description = "Название пиццы на момент заказа", example = "Маргарита")
        private final String pizzaName;

        @Schema(description = "Количество", example = "2")
        private final Integer quantity;

        @Schema(description = "Цена за единицу на момент заказа", example = "645.00")
        private final BigDecimal unitPrice;
    }
}
