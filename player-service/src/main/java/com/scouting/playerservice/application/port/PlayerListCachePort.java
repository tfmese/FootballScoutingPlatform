package com.scouting.playerservice.application.port;

import com.scouting.playerservice.domain.Player;

import java.util.List;
import java.util.Optional;

/**
 * Oyuncu listesi önbelleği için port tanımı.
 */
public interface PlayerListCachePort {

    Optional<List<Player>> getCachedPlayerList();

    void putCachedPlayerList(List<Player> players);

    void evictCachedPlayerList();
}
