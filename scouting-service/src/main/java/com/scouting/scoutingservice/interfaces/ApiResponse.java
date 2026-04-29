package com.scouting.scoutingservice.interfaces;

public record ApiResponse<T>(
        String message,
        T data
) {
}
