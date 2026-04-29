package com.scouting.scoutingservice.interfaces;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record CreateScoutReportRequest(
        @NotBlank(message = "playerName is required")
        String playerName,
        @NotBlank(message = "position is required")
        String position,
        @Min(value = 1, message = "potentialScore must be at least 1")
        @Max(value = 100, message = "potentialScore must be at most 100")
        int potentialScore,
        @NotBlank(message = "notes is required")
        String notes
) {
}
