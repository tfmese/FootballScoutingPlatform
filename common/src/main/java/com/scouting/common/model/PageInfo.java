package com.scouting.common.model;

public record PageInfo(
        int page,
        int size,
        long totalItems,
        int totalPages
) {
    public static PageInfo singlePage(int size) {
        return new PageInfo(0, size, size, size == 0 ? 0 : 1);
    }
}

