package edu.rutmiit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record PatchMenuItemRequest(

        @Schema(description = "Название пиццы", example = "Маргарита", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        @Size(max = 200, message = "Название не может превышать 200 символов")
        String name,

        @Schema(description = "Описание (состав, ингредиенты)", example = "Томатный соус, моцарелла, базилик")
        @Size(max = 1000, message = "Описание не может превышать 1000 символов")
        String description,

        @Schema(description = "Цена за единицу", example = "645.00", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        @DecimalMin(value = "0.0", inclusive = false, message = "Цена должна быть положительной")
        @Digits(integer = 8, fraction = 2, message = "Некорректный формат цены")
        BigDecimal price,

        @Schema(description = "Доступна ли пицца к заказу", example = "true")
        Boolean available
) { }
