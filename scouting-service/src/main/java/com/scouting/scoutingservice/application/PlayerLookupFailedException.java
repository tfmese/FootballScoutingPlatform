package com.scouting.scoutingservice.application;

public final class PlayerLookupFailedException extends RuntimeException {

    public PlayerLookupFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public PlayerLookupFailedException(String message) {
        super(message);
    }
}
