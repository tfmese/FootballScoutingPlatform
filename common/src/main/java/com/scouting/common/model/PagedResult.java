package com.scouting.common.model;

import java.util.List;

public record PagedResult<T>(
        List<T> items,
        PageInfo page
) {
    public static <T> PagedResult<T> of(List<T> items) {
        List<T> safeItems = items == null ? List.of() : List.copyOf(items);
        return new PagedResult<>(safeItems, PageInfo.singlePage(safeItems.size()));
    }
}

