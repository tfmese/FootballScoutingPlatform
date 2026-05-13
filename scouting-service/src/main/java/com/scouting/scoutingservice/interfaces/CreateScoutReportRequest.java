package com.scouting.scoutingservice.interfaces;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CreateScoutReportRequest(
        @Pattern(
                regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
                message = "playerId must be a valid UUID when provided"
        )
        String playerId,
        @NotBlank(message = "playerName is required")
        String playerName,
        @NotBlank(message = "position is required")
        String position,
        @Min(value = 15, message = "playerAge must be at least 15")
        @Max(value = 50, message = "playerAge must be at most 50")
        int playerAge,
        @Min(value = 1, message = "technicalScore must be at least 1")
        @Max(value = 100, message = "technicalScore must be at most 100")
        int technicalScore,
        @Min(value = 1, message = "physicalScore must be at least 1")
        @Max(value = 100, message = "physicalScore must be at most 100")
        int physicalScore,
        @Min(value = 1, message = "tacticalScore must be at least 1")
        @Max(value = 100, message = "tacticalScore must be at most 100")
        int tacticalScore,
        @Min(value = 1, message = "mentalScore must be at least 1")
        @Max(value = 100, message = "mentalScore must be at most 100")
        int mentalScore,
        @Min(value = 0, message = "expectedFee must be at least 0")
        long expectedFee,
        @NotBlank(message = "recommendation is required")
        String recommendation,
        @NotBlank(message = "notes is required")
        String notes
) {
}
