package com.scouting.playerservice.application;

import com.scouting.playerservice.application.port.PlayerJdbcQueryPort;
import com.scouting.playerservice.domain.Player;
import com.scouting.playerservice.domain.PlayerNotFoundException;
import com.scouting.playerservice.domain.PlayerRepository;
import com.scouting.playerservice.infrastructure.cache.NoOpPlayerListCacheAdapter;
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
        InMemoryRepositoryStub repository = new InMemoryRepositoryStub();
        playerService = new PlayerService(
                repository,
                new NoOpPlayerListCacheAdapter(),
                new InMemoryJdbcQueryStub(repository)
        );
    }

    @Test
    void createPlayerShouldReturnGeneratedId() {
        Player created = playerService.createPlayer("Orkun Kokcu", "CM", 25, "Benfica", "Right");

        assertNotNull(created.getId());
        assertEquals("Orkun Kokcu", created.getName());
        assertEquals("CM", created.getPosition());
        assertEquals(25, created.getAge());
        assertEquals("Benfica", created.getClub());
        assertEquals("Right", created.getPreferredFoot());
    }

    @Test
    void getPlayerByIdWhenExistsShouldReturnPlayer() {
        Player created = playerService.createPlayer("Kenan Yildiz", "LW", 20, "Juventus", "Both");

        Player found = playerService.getPlayerById(created.getId());

        assertEquals(created.getId(), found.getId());
        assertEquals("Kenan Yildiz", found.getName());
    }

    @Test
    void getPlayerByIdWhenNotExistsShouldThrowNotFound() {
        UUID unknownId = UUID.randomUUID();

        assertThrows(PlayerNotFoundException.class, () -> playerService.getPlayerById(unknownId));
    }

    @Test
    void getAllPlayersShouldReturnAllSavedPlayers() {
        playerService.createPlayer("Player One", "CB", 24, "Club One", "Left");
        playerService.createPlayer("Player Two", "RB", 22, "Club Two", "Right");

        List<Player> players = playerService.getAllPlayers();

        assertEquals(2, players.size());
    }

    @Test
    void getAllPlayersViaJdbcShouldReturnAllSavedPlayers() {
        playerService.createPlayer("Player One", "CB", 24, "Club One", "Left");
        playerService.createPlayer("Player Two", "RB", 22, "Club Two", "Right");

        List<Player> players = playerService.getAllPlayersViaJdbc();

        assertEquals(2, players.size());
    }

    @Test
    void updatePlayerWhenExistsShouldPersistChanges() {
        Player created = playerService.createPlayer("Old Name", "CM", 21, "Old Club", "Left");

        Player updated = playerService.updatePlayer(created.getId(), "New Name", "CDM", 22, "New Club", "Both");

        assertEquals(created.getId(), updated.getId());
        assertEquals("New Name", updated.getName());
        assertEquals("CDM", updated.getPosition());
        assertEquals(22, updated.getAge());
        assertEquals("New Club", updated.getClub());
        assertEquals("Both", updated.getPreferredFoot());
    }

    @Test
    void deletePlayerWhenExistsShouldRemovePlayer() {
        Player created = playerService.createPlayer("Delete Me", "LW", 23, "Delete FC", "Right");

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

    private static final class InMemoryJdbcQueryStub implements PlayerJdbcQueryPort {
        private final InMemoryRepositoryStub repository;

        private InMemoryJdbcQueryStub(InMemoryRepositoryStub repository) {
            this.repository = repository;
        }

        @Override
        public List<Player> findAllPlayers() {
            return repository.findAll();
        }
    }
}
