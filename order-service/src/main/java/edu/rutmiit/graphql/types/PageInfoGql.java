package edu.rutmiit.graphql.types;

public record PageInfoGql(
        Integer pageNumber,
        Integer pageSize,
        Integer totalPages,
        Boolean last
) {}
