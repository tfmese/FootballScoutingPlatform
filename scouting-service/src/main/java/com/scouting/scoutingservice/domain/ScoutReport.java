package com.scouting.scoutingservice.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Document(collection = "scout_reports")
public class ScoutReport {
    @Id
    private String id;
    private UUID playerId;
    private String playerName;
    private String position;
    private int potentialScore;
    private String notes;

    protected ScoutReport() {
    }

    public ScoutReport(String id, String playerName, String position, int potentialScore, String notes) {
        this(id, null, playerName, position, potentialScore, notes);
    }

    public ScoutReport(String id, UUID playerId, String playerName, String position, int potentialScore, String notes) {
        this.id = id;
        this.playerId = playerId;
        this.playerName = playerName;
        this.position = position;
        this.potentialScore = potentialScore;
        this.notes = notes;
    }

    public static ScoutReport create(UUID playerId, String playerName, String position, int potentialScore, String notes) {
        return new ScoutReport(null, playerId, playerName, position, potentialScore, notes);
    }

    public void update(UUID playerId, String playerName, String position, int potentialScore, String notes) {
        this.playerId = playerId;
        this.playerName = playerName;
        this.position = position;
        this.potentialScore = potentialScore;
        this.notes = notes;
    }

    public String getId() {
        return id;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getPosition() {
        return position;
    }

    public int getPotentialScore() {
        return potentialScore;
    }

    public String getNotes() {
        return notes;
    }
}
