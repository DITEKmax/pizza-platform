package edu.rutmiit.endpoints;

import edu.rutmiit.dto.request.MenuItemRequest;
import edu.rutmiit.dto.request.PatchMenuItemRequest;
import edu.rutmiit.dto.request.UpdateMenuItemRequest;
import edu.rutmiit.dto.response.ErrorResponse;
import edu.rutmiit.dto.response.MenuItemResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Menu", description = "Управление меню пиццерии")
@RequestMapping(
        value = "/api/menu",
        produces = MediaType.APPLICATION_JSON_VALUE
)
public interface MenuApi {

    @Operation(summary = "Получить позицию меню по ID")
    @ApiResponse(responseCode = "200", description = "Позиция найдена")
    @ApiResponse(responseCode = "404", description = "Позиция не найдена",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @GetMapping("/{id}")
    EntityModel<MenuItemResponse> getMenuItemById(
            @Parameter(description = "ID позиции меню", required = true, example = "7") @PathVariable Long id
    );


    @Operation(
            summary = "Список позиций меню",
            description = """
                    Возвращает постраничный список позиций меню с HATEOAS-ссылками.
                    Поддерживает фильтр по доступности и поиск по названию.
                    """
    )
    @ApiResponse(responseCode = "200", description = "Постраничный список позиций")
    @GetMapping
    PagedModel<EntityModel<MenuItemResponse>> getAllMenuItems(
            @Parameter(description = "Только доступные к заказу", example = "true")
            @RequestParam(required = false) Boolean availableOnly,

            @Parameter(description = "Поиск по названию (substring, case-insensitive)", example = "Маргарита")
            @RequestParam(required = false) String nameSearch,

            @Parameter(description = "Номер страницы (0..N)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Размер страницы", example = "20")
            @RequestParam(defaultValue = "20") int size

    );

    @Operation(summary = "Создать позицию меню")
    @ApiResponse(responseCode = "201", description = "Позиция создана. Location header содержит URI нового ресурса.")
    @ApiResponse(responseCode = "400", description = "Ошибка валидации",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    ResponseEntity<EntityModel<MenuItemResponse>> createMenuItem(@Valid @RequestBody MenuItemRequest request);


    @Operation(
            summary = "Полное обновление позиции (PUT)",
            description = "Заменяет все поля позиции меню. Для частичного обновления используйте PATCH."
    )
    @ApiResponse(responseCode = "200", description = "Позиция обновлена")
    @ApiResponse(responseCode = "400", description = "Ошибка валидации",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Позиция не найдена",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    EntityModel<MenuItemResponse> updateMenuItem(
            @Parameter(description = "ID позиции меню", required = true, example = "7") @PathVariable Long id,
            @Valid @RequestBody UpdateMenuItemRequest request
    );

    @Operation(
            summary = "Частичное обновление позиции (PATCH)",
            description = """
                    Обновляет только переданные поля (семантика JSON Merge Patch, RFC 7396).
                    Непереданные поля остаются без изменений. Удобно, например, чтобы быстро
                    переключить available или поменять только цену.
                    """
    )
    @ApiResponse(responseCode = "200", description = "Позиция обновлена")
    @ApiResponse(responseCode = "400", description = "Ошибка валидации",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Позиция не найдена",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PatchMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    EntityModel<MenuItemResponse> patchMenuItem(
            @Parameter(description = "ID позиции меню", required = true, example = "7") @PathVariable Long id,
            @Valid @RequestBody PatchMenuItemRequest request
    );

    @Operation(summary = "Удалить позицию меню")
    @ApiResponse(responseCode = "204", description = "Позиция удалена")
    @ApiResponse(responseCode = "404", description = "Позиция не найдена",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteMenuItem(
            @Parameter(description = "ID позиции меню", required = true, example = "7") @PathVariable Long id
    );
}
