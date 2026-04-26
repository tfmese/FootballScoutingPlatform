package com.scouting.playerservice.domain;

import java.util.UUID;

public class PlayerNotFoundException extends RuntimeException {
    public PlayerNotFoundException(UUID playerId) {
        super("Player not found: " + playerId);
    }
}
