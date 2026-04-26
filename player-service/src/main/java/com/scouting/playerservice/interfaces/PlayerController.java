package com.scouting.playerservice.interfaces;

import com.scouting.playerservice.application.PlayerService;
import com.scouting.playerservice.domain.Player;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/players")
public class PlayerController {

    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Player>> createPlayer(@RequestBody CreatePlayerRequest request) {
        Player created = playerService.createPlayer(request.name(), request.position(), request.age());
        return created(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Player>> getPlayerById(@PathVariable UUID id) {
        Player player = playerService.getPlayerById(id);
        return ok(player);
    }

    private ResponseEntity<ApiResponse<Player>> created(Player player) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>("Player created", player));
    }

    private ResponseEntity<ApiResponse<Player>> ok(Player player) {
        return ResponseEntity.ok(new ApiResponse<>("Player retrieved", player));
    }
}
