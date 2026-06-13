package edu.rutmiit.dto.request;

import edu.rutmiit.validation.ValidPhone;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.List;

@Schema(description = "Запрос на создание заказа пиццы")
public record CreateOrderRequest(

        @Schema(description = "ID клиента", example = "42", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "ID клиента обязателен")
        Long customerId,

        @Schema(description = "Адрес доставки", example = "ул. Пушкина, д. 10, кв. 5", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Адрес доставки не может быть пустым")
        @Size(max = 500, message = "Адрес не может превышать 500 символов")
        String deliveryAddress,

        @Schema(description = "Позиции заказа", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotEmpty(message = "Заказ должен содержать хотя бы одну позицию")
        @Valid
        List<OrderItemRequest> items,

        @Schema(description = "Номер телефона", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Необходимо указать телефон")
        @ValidPhone
        String phoneNumber
) {

    @Schema(description = "Позиция заказа")
    public record OrderItemRequest(

            @Schema(description = "ID пиццы из меню", example = "7", requiredMode = Schema.RequiredMode.REQUIRED)
            @NotNull(message = "ID пиццы обязателен")
            Long pizzaId,

            @Schema(description = "Количество", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
            @NotNull(message = "Количество обязательно")
            @Min(value = 1, message = "Количество должно быть не меньше 1")
            @Max(value = 50, message = "Слишком большое количество")
            Integer quantity
    ) {}
}