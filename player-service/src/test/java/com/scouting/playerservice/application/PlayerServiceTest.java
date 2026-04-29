package com.scouting.playerservice.application;

import com.scouting.playerservice.domain.Player;
import com.scouting.playerservice.domain.PlayerRepository;
import com.scouting.playerservice.domain.PlayerNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PlayerServiceTest {

    private PlayerService playerService;

    @BeforeEach
    void setUp() {
        playerService = new PlayerService(new InMemoryRepositoryStub());
    }

    @Test
    void createPlayerShouldReturnGeneratedId() {
        Player created = playerService.createPlayer("Orkun Kökçü", "CM", 25);

        assertNotNull(created.getId());
        assertEquals("Orkun Kökçü", created.getName());
        assertEquals("CM", created.getPosition());
        assertEquals(25, created.getAge());
    }

    @Test
    void getPlayerByIdWhenExistsShouldReturnPlayer() {
        Player created = playerService.createPlayer("Kenan Yıldız", "LW", 20);

        Player found = playerService.getPlayerById(created.getId());

        assertEquals(created.getId(), found.getId());
        assertEquals("Kenan Yıldız", found.getName());
    }

    @Test
    void getPlayerByIdWhenNotExistsShouldThrowNotFound() {
        UUID unknownId = UUID.randomUUID();

        assertThrows(PlayerNotFoundException.class, () -> playerService.getPlayerById(unknownId));
    }

    @Test
    void getAllPlayersShouldReturnAllSavedPlayers() {
        playerService.createPlayer("Player One", "CB", 24);
        playerService.createPlayer("Player Two", "RB", 22);

        List<Player> players = playerService.getAllPlayers();

        assertEquals(2, players.size());
    }

    @Test
    void updatePlayerWhenExistsShouldPersistChanges() {
        Player created = playerService.createPlayer("Old Name", "CM", 21);

        Player updated = playerService.updatePlayer(created.getId(), "New Name", "CDM", 22);

        assertEquals(created.getId(), updated.getId());
        assertEquals("New Name", updated.getName());
        assertEquals("CDM", updated.getPosition());
        assertEquals(22, updated.getAge());
    }

    @Test
    void deletePlayerWhenExistsShouldRemovePlayer() {
        Player created = playerService.createPlayer("Delete Me", "LW", 23);

        playerService.deletePlayer(created.getId());

        assertThrows(PlayerNotFoundException.class, () -> playerService.getPlayerById(created.getId()));
    }

    private static final class InMemoryRepositoryStub implements PlayerRepository {
        private final Map<UUID, Player> storage = new HashMap<>();

        @Override
        public Player save(Player player) {
            storage.put(player.getId(), player);
            return player;
        }

        @Override
        public Optional<Player> findById(UUID id) {
            return Optional.ofNullable(storage.get(id));
        }

        @Override
        public List<Player> findAll() {
            return new ArrayList<>(storage.values());
        }

        @Override
        public void deleteById(UUID id) {
            storage.remove(id);
        }
    }
}
