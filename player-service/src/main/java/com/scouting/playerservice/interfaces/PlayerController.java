package com.scouting.playerservice.interfaces;

import com.scouting.common.model.PagedResult;
import com.scouting.playerservice.application.PlayerService;
import com.scouting.playerservice.domain.Player;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/players")
public class PlayerController {

    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Player>> createPlayer(@Valid @RequestBody CreatePlayerRequest request) {
        Player created = playerService.createPlayer(request.name(), request.position(), request.age(), request.club(), request.preferredFoot());
        return ResponseFactory.created("Player created", created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Player>> getPlayerById(@PathVariable UUID id) {
        Player player = playerService.getPlayerById(id);
        return ResponseFactory.ok("Player retrieved", player);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResult<Player>>> getAllPlayers() {
        List<Player> players = playerService.getAllPlayers();
        return ResponseFactory.ok("Players retrieved", PagedResult.of(players));
    }

    @GetMapping("/jdbc")
    public ResponseEntity<ApiResponse<PagedResult<Player>>> getAllPlayersViaJdbc() {
        List<Player> players = playerService.getAllPlayersViaJdbc();
        return ResponseFactory.ok("Players retrieved via JDBC", PagedResult.of(players));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Player>> updatePlayer(
            @PathVariable UUID id,
            @Valid @RequestBody UpdatePlayerRequest request
    ) {
        Player updated = playerService.updatePlayer(id, request.name(), request.position(), request.age(), request.club(), request.preferredFoot());
        return ResponseFactory.ok("Player updated", updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlayer(@PathVariable UUID id) {
        playerService.deletePlayer(id);
        return ResponseEntity.noContent().build();
    }
}
