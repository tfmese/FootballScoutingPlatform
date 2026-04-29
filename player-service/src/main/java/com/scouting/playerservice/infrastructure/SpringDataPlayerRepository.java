package com.scouting.playerservice.infrastructure;

import com.scouting.playerservice.domain.Player;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SpringDataPlayerRepository extends JpaRepository<Player, UUID> {
}
