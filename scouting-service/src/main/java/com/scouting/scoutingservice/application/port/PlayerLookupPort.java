package com.scouting.scoutingservice.application.port;

import java.util.Optional;

public interface PlayerLookupPort {

    Optional<PlayerSnapshot> findById(String playerId);
}
