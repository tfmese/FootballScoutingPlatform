package com.scouting.playerservice.interfaces;

import com.scouting.playerservice.application.PlayerService;
import com.scouting.playerservice.domain.Player;
import com.scouting.playerservice.domain.PlayerNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PlayerControllerTest {

    private PlayerService playerService;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        playerService = mock(PlayerService.class);
        PlayerController controller = new PlayerController(playerService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void postPlayersShouldReturnCreated() throws Exception {
        Player player = new Player(UUID.randomUUID(), "Amir Murillo", "RB", 30);
        when(playerService.createPlayer(anyString(), anyString(), anyInt())).thenReturn(player);

        mockMvc.perform(post("/players")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Amir Murillo\",\"position\":\"RB\",\"age\":30}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.name").value("Amir Murillo"));
    }

    @Test
    void getPlayerByIdWhenExistsShouldReturnOk() throws Exception {
        UUID playerId = UUID.randomUUID();
        Player player = new Player(playerId, "Emmanuel Agbadou", "CB", 28);
        when(playerService.getPlayerById(eq(playerId))).thenReturn(player);

        mockMvc.perform(get("/players/{id}", playerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(playerId.toString()));
    }

    @Test
    void getPlayerByIdWhenNotFoundShouldReturn404() throws Exception {
        UUID playerId = UUID.randomUUID();
        when(playerService.getPlayerById(eq(playerId))).thenThrow(new PlayerNotFoundException(playerId));

        mockMvc.perform(get("/players/{id}", playerId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Player not found: " + playerId));
    }

    @Test
    void getPlayersShouldReturnAllPlayers() throws Exception {
        Player first = new Player(UUID.randomUUID(), "A", "CB", 24);
        Player second = new Player(UUID.randomUUID(), "B", "RB", 25);
        when(playerService.getAllPlayers()).thenReturn(List.of(first, second));

        mockMvc.perform(get("/players"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    void putPlayerShouldReturnOk() throws Exception {
        UUID playerId = UUID.randomUUID();
        Player updated = new Player(playerId, "Updated", "CM", 27);
        when(playerService.updatePlayer(eq(playerId), anyString(), anyString(), anyInt())).thenReturn(updated);

        mockMvc.perform(put("/players/{id}", playerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Updated\",\"position\":\"CM\",\"age\":27}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Updated"));
    }

    @Test
    void deletePlayerShouldReturnNoContent() throws Exception {
        UUID playerId = UUID.randomUUID();
        doNothing().when(playerService).deletePlayer(eq(playerId));

        mockMvc.perform(delete("/players/{id}", playerId))
                .andExpect(status().isNoContent());
    }

    @Test
    void postPlayersWithInvalidBodyShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/players")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\",\"position\":\"RB\",\"age\":10}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }
}
