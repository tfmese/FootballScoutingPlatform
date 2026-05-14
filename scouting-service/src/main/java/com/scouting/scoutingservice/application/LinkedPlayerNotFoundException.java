package com.scouting.scoutingservice.application;

public final class LinkedPlayerNotFoundException extends RuntimeException {

    public LinkedPlayerNotFoundException(String playerId) {
        super("Linked player not found: " + playerId);
    }
}
