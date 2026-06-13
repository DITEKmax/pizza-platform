package edu.rutmiit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record UpdateMenuItemRequest(

        @Schema(description = "Название пиццы", example = "Маргарита", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Название не может быть пустым")
        @Size(max = 200, message = "Название не может превышать 200 символов")
        String name,

        @Schema(description = "Описание (состав, ингредиенты)", example = "Томатный соус, моцарелла, базилик", requiredMode = Schema.RequiredMode.REQUIRED)
        @Size(max = 1000, message = "Описание не может превышать 1000 символов")
        @NotBlank(message = "Описание пиццы не может быть пустым")
        String description,

        @Schema(description = "Цена за единицу", example = "645.00", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Цена обязательна")
        @DecimalMin(value = "0.0", inclusive = false, message = "Цена должна быть положительной")
        @Digits(integer = 8, fraction = 2, message = "Некорректный формат цены")
        BigDecimal price,

        @Schema(description = "Доступна ли пицца к заказу", example = "true")
        @NotNull(message = "Видимость пиццы обязательна")
        Boolean available

) { }
