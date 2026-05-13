package com.scouting.playerservice.interfaces;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CreatePlayerRequest(
        @NotBlank(message = "name is required")
        String name,
        @NotBlank(message = "position is required")
        String position,
        @Min(value = 15, message = "age must be at least 15")
        @Max(value = 50, message = "age must be at most 50")
        int age,
        @NotBlank(message = "club is required")
        String club,
        @NotBlank(message = "preferredFoot is required")
        @Pattern(regexp = "Left|Right|Both", message = "preferredFoot must be Left, Right or Both")
        String preferredFoot
) {
}
