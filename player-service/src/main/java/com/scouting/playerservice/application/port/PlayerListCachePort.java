package com.scouting.playerservice.application.port;

import com.scouting.playerservice.domain.Player;

import java.util.List;
import java.util.Optional;

/**
 * Oyuncu listesi önbelleği (DIP): uygulama katmanı Redis/JVM gibi detayları bilmez.
 */
public interface PlayerListCachePort {

    Optional<List<Player>> getCachedPlayerList();

    void putCachedPlayerList(List<Player> players);

    void evictCachedPlayerList();
}
