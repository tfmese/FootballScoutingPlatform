package com.scouting.playerservice.interfaces;

public record CreatePlayerRequest(
        String name,
        String position,
        int age
) {
}
