package com.scouting.playerservice.infrastructure.cache;

import com.scouting.playerservice.application.port.PlayerListCachePort;
import com.scouting.playerservice.domain.Player;

import java.util.List;
import java.util.Optional;

public final class NoOpPlayerListCacheAdapter implements PlayerListCachePort {

    @Override
    public Optional<List<Player>> getCachedPlayerList() {
        return Optional.empty();
    }

    @Override
    public void putCachedPlayerList(List<Player> players) {
        // no-op
    }

    @Override
    public void evictCachedPlayerList() {
        // no-op
    }
}
