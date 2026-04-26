package com.scouting.playerservice.application;

import com.scouting.playerservice.domain.Player;
import com.scouting.playerservice.domain.PlayerNotFoundException;
import com.scouting.playerservice.domain.PlayerRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PlayerService {

    private final PlayerRepository playerRepository;

    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public Player createPlayer(String name, String position, int age) {
        return playerRepository.save(newPlayer(name, position, age));
    }

    public Player getPlayerById(UUID playerId) {
        return playerRepository.findById(playerId)
                .orElseThrow(() -> new PlayerNotFoundException(playerId));
    }

    private Player newPlayer(String name, String position, int age) {
        return Player.create(name, position, age);
    }
}
