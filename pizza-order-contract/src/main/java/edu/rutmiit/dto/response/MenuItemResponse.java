package edu.rutmiit.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.math.BigDecimal;

@Getter
@Builder
@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Relation(collectionRelation = "menuItems", itemRelation = "menuItem")
@Schema(description = "Информация о позиции меню")
public class MenuItemResponse extends RepresentationModel<MenuItemResponse> {

    @Schema(description = "Уникальный идентификатор позиции", example = "7")
    private final Long id;

    @Schema(description = "Название пиццы", example = "Маргарита")
    private final String name;

    @Schema(description = "Описание (состав)", example = "Томатный соус, моцарелла, базилик")
    private final String description;

    @Schema(description = "Цена за единицу", example = "645.00")
    private final BigDecimal price;

    @Schema(description = "Доступна ли к заказу", example = "true")
    private final Boolean available;
}
