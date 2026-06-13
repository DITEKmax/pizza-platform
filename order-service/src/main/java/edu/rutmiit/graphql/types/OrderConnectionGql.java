package edu.rutmiit.graphql.types;


import edu.rutmiit.dto.response.OrderResponse;

import java.util.List;

public record OrderConnectionGql(
        List<OrderResponse> content,
        PageInfoGql pageInfo,
        Integer totalElements
) {
}
