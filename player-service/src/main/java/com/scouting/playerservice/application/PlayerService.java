package com.scouting.playerservice.application;

import com.scouting.playerservice.application.port.PlayerListCachePort;
import com.scouting.playerservice.domain.Player;
import com.scouting.playerservice.domain.PlayerNotFoundException;
import com.scouting.playerservice.domain.PlayerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PlayerService {

    private final PlayerRepository playerRepository;
    private final PlayerListCachePort playerListCache;

    public PlayerService(PlayerRepository playerRepository, PlayerListCachePort playerListCache) {
        this.playerRepository = playerRepository;
        this.playerListCache = playerListCache;
    }

    public Player createPlayer(String name, String position, int age) {
        Player created = playerRepository.save(newPlayer(name, position, age));
        playerListCache.evictCachedPlayerList();
        return created;
    }

    public Player getPlayerById(UUID playerId) {
        return playerRepository.findById(playerId)
                .orElseThrow(() -> new PlayerNotFoundException(playerId));
    }

    public List<Player> getAllPlayers() {
        return playerListCache.getCachedPlayerList().orElseGet(() -> {
            List<Player> all = playerRepository.findAll();
            playerListCache.putCachedPlayerList(all);
            return all;
        });
    }

    public Player updatePlayer(UUID playerId, String name, String position, int age) {
        Player existing = getPlayerById(playerId);
        existing.update(name, position, age);
        Player saved = playerRepository.save(existing);
        playerListCache.evictCachedPlayerList();
        return saved;
    }

    public void deletePlayer(UUID playerId) {
        Player existing = getPlayerById(playerId);
        playerRepository.deleteById(existing.getId());
        playerListCache.evictCachedPlayerList();
    }

    private Player newPlayer(String name, String position, int age) {
        return Player.create(name, position, age);
    }
}
