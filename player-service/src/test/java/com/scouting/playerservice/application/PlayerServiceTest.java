package com.scouting.playerservice.application;

import com.scouting.playerservice.domain.Player;
import com.scouting.playerservice.domain.PlayerRepository;
import com.scouting.playerservice.domain.PlayerNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
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
    }
}
