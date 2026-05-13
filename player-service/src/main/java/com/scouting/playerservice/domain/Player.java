package com.scouting.playerservice.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "players")
public class Player {
    @Id
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String position;

    @Column(nullable = false)
    private int age;

    @Column(length = 120)
    private String club;

    // Keep the column backward-compatible with existing rows while the data is backfilled.
    @Column(name = "preferred_foot", length = 16)
    private String preferredFoot;

    protected Player() {
    }

    public Player(UUID id, String name, String position, int age, String club, String preferredFoot) {
        this.id = id;
        this.name = name;
        this.position = position;
        this.age = age;
        this.club = normalizeClub(club);
        this.preferredFoot = normalizePreferredFoot(preferredFoot);
    }

    public static Player create(String name, String position, int age, String club, String preferredFoot) {
        return new Player(UUID.randomUUID(), name, position, age, club, preferredFoot);
    }

    public void update(String name, String position, int age, String club, String preferredFoot) {
        this.name = name;
        this.position = position;
        this.age = age;
        this.club = normalizeClub(club);
        this.preferredFoot = normalizePreferredFoot(preferredFoot);
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

    public String getClub() {
        return normalizeClub(club);
    }

    public String getPreferredFoot() {
        return normalizePreferredFoot(preferredFoot);
    }

    private static String normalizeClub(String club) {
        if (club == null) {
            return "";
        }
        return club.trim();
    }

    private static String normalizePreferredFoot(String preferredFoot) {
        if (preferredFoot == null || preferredFoot.isBlank()) {
            return "Right";
        }
        return preferredFoot.trim();
    }
}
