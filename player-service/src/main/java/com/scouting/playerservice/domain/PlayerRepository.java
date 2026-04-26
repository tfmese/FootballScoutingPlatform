package com.scouting.playerservice.domain;

import java.util.Optional;
import java.util.UUID;

public interface PlayerRepository {
    Player save(Player player);

    Optional<Player> findById(UUID id);
}
