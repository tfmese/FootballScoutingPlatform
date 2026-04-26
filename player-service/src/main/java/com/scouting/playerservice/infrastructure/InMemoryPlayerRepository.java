package com.scouting.playerservice.infrastructure;

import com.scouting.playerservice.domain.Player;
import com.scouting.playerservice.domain.PlayerRepository;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryPlayerRepository implements PlayerRepository {
    private final Map<UUID, Player> players = new ConcurrentHashMap<>();

    @Override
    public Player save(Player player) {
        players.put(player.getId(), player);
        return player;
    }

    @Override
    public Optional<Player> findById(UUID id) {
        return Optional.ofNullable(players.get(id));
    }
}
