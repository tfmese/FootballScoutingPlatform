package com.scouting.playerservice.application.port;

import com.scouting.playerservice.domain.Player;

import java.util.List;

/**
 * Sunum ve doğrulama amacıyla oyuncuları doğrudan SQL/JDBC ile okur.
 */
public interface PlayerJdbcQueryPort {

    List<Player> findAllPlayers();
}
