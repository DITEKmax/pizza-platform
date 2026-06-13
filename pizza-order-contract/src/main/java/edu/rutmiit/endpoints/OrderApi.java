package edu.rutmiit.endpoints;

import edu.rutmiit.dto.enums.OrderStatus;
import edu.rutmiit.dto.request.CreateOrderRequest;
import edu.rutmiit.dto.response.ErrorResponse;
import edu.rutmiit.dto.response.OrderResponse;
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

@Tag(name = "Orders", description = "Оформление и просмотр заказов пиццы")
@RequestMapping(
        value = "/api/orders",
        produces = MediaType.APPLICATION_JSON_VALUE
)
public interface OrderApi {

    @Operation(
            summary = "Создать заказ",
            description = """
                    Точка входа всей системы. Принимает заказ, сохраняет его со статусом CREATED
                    и публикует событие OrderCreated в Kafka. Дальнейшая обработка 
                    (готовка, доставка) идёт асинхронно — клиент опрашивает статус через GET.
                    """
    )
    @ApiResponse(responseCode = "201", description = "Заказ создан. Location header содержит URI нового ресурса.")
    @ApiResponse(responseCode = "400", description = "Ошибка валидации",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    ResponseEntity<EntityModel<OrderResponse>> createOrder(@Valid @RequestBody CreateOrderRequest request);

    @Operation(summary = "Получить заказ по ID")
    @ApiResponse(responseCode = "200", description = "Заказ найден")
    @ApiResponse(responseCode = "404", description = "Заказ не найден",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @GetMapping("/{id}")
    EntityModel<OrderResponse> getOrderById(
            @Parameter(description = "ID заказа", required = true, example = "1") @PathVariable Long id
    );

    @Operation(
            summary = "Список заказов",
            description = "Постраничный список заказов с фильтром по клиенту и по статусу."
    )
    @ApiResponse(responseCode = "200", description = "Постраничный список заказов")
    @GetMapping
    PagedModel<EntityModel<OrderResponse>> getAllOrders(
            @Parameter(description = "Фильтр по ID клиента")
            @RequestParam(required = false) Long customerId,

            @Parameter(description = "Фильтр по статусу", example = "COOKING")
            @RequestParam(required = false) OrderStatus status,

            @Parameter(description = "Номер страницы (0..N)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Размер страницы", example = "20")
            @RequestParam(defaultValue = "20") int size
    );

    @Operation(
            summary = "Отменить заказ",
            description = """
                    Отменяет заказ, если он ещё не ушёл в доставку. Меняет статус на CANCELLED
                    и публикует событие OrderCancelled. Если заказ уже в пути или доставлен —
                    возвращает 409 Conflict.
                    """
    )
    @ApiResponse(responseCode = "200", description = "Заказ отменён")
    @ApiResponse(responseCode = "404", description = "Заказ не найден",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "409", description = "Заказ нельзя отменить на текущем этапе",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping("/{id}/cancel")
    EntityModel<OrderResponse> cancelOrder(
            @Parameter(description = "ID заказа", required = true, example = "1") @PathVariable Long id
    );
}
