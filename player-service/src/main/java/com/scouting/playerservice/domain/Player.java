package com.scouting.playerservice.domain;

import java.util.UUID;

public class Player {
    private final UUID id;
    private final String name;
    private final String position;
    private final int age;

    public Player(UUID id, String name, String position, int age) {
        this.id = id;
        this.name = name;
        this.position = position;
        this.age = age;
    }

    public static Player create(String name, String position, int age) {
        return new Player(UUID.randomUUID(), name, position, age);
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPosition() {
        return position;
    }

    public int getAge() {
        return age;
    }
}
