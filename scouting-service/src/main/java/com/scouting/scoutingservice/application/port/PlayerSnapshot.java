package com.scouting.scoutingservice.application.port;

public record PlayerSnapshot(
        String id,
        String name,
        String position,
        int age
) {
}
