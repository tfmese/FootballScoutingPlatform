package com.scouting.playerservice.domain;

import java.util.Optional;
import java.util.UUID;
import java.util.List;

public interface PlayerRepository {
    Player save(Player player);

    Optional<Player> findById(UUID id);

    List<Player> findAll();

    void deleteById(UUID id);
}
