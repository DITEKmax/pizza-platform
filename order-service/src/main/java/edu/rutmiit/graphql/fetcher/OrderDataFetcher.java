package edu.rutmiit.graphql.fetcher;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import edu.rutmiit.dto.enums.OrderStatus;
import edu.rutmiit.dto.request.CreateOrderRequest;
import edu.rutmiit.dto.response.OrderResponse;
import edu.rutmiit.dto.response.PagedResponse;
import edu.rutmiit.graphql.types.CreateOrderInputGql;
import edu.rutmiit.graphql.types.OrderConnectionGql;
import edu.rutmiit.graphql.types.OrderFilterGql;
import edu.rutmiit.graphql.types.PageInfoGql;
import edu.rutmiit.service.OrderService;


@DgsComponent
public class OrderDataFetcher {

    private final OrderService orderService;

    public OrderDataFetcher(OrderService orderService) {
        this.orderService = orderService;
    }

    @DgsQuery
    public OrderResponse order(@InputArgument String id){
        return orderService.findOrderById(Long.parseLong(id));
    }

    @DgsQuery
    public OrderConnectionGql orders(@InputArgument OrderFilterGql filter,
                                     @InputArgument Integer page, @InputArgument Integer size){
        int pageNum = page != null ? page : 0;
        int pageSize = size != null ? size : 20;

        Long customerId = null;
        OrderStatus status = null;

        if (filter != null) {
            customerId = filter.customerId() != null ? Long.parseLong(filter.customerId()) : null;
            status = filter.status();
        }

        PagedResponse<OrderResponse> paged = orderService.findAllOrders(customerId, status, pageNum, pageSize);

        return new OrderConnectionGql(
                paged.content(),
                new PageInfoGql(paged.pageNumber(), paged.pageSize(), paged.totalPages(), paged.last()),
                (int) paged.totalElements()
        );
    }

    @DgsMutation
    public OrderResponse createOrder(@InputArgument CreateOrderInputGql input){
        CreateOrderRequest request = new CreateOrderRequest(
                Long.parseLong(input.customerId()),
                input.deliveryAddress(),
                input.items().stream().map(i -> new CreateOrderRequest.OrderItemRequest(
                        Long.parseLong(i.pizzaId()),
                        i.quantity()))
                        .toList(),
                input.phoneNumber()
        );
        return orderService.createOrder(request);
    }

    @DgsMutation
    public OrderResponse cancelOrder(@InputArgument String id){
        return orderService.cancelOrder(Long.parseLong(id));
    }
}
