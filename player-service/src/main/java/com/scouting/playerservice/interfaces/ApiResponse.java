package com.scouting.playerservice.interfaces;

public record ApiResponse<T>(
        String message,
        T data
) {
}
