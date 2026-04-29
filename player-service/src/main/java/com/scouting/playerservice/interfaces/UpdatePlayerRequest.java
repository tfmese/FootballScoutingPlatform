package com.scouting.playerservice.interfaces;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record UpdatePlayerRequest(
        @NotBlank(message = "name is required")
        String name,
        @NotBlank(message = "position is required")
        String position,
        @Min(value = 15, message = "age must be at least 15")
        @Max(value = 50, message = "age must be at most 50")
        int age
) {
}
