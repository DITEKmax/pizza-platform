package edu.rutmiit.graphql.types;

import edu.rutmiit.dto.response.MenuItemResponse;

import java.util.List;

public record MenuItemConnectionGql(
        List<MenuItemResponse> content,
        PageInfoGql pageInfo,
        Long totalElements
) {
}
