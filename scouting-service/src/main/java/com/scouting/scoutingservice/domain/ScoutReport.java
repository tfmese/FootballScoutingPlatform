package com.scouting.scoutingservice.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "scout_reports")
public class ScoutReport {
    @Id
    private String id;
    private String playerName;
    private String position;
    private int potentialScore;
    private String notes;

    protected ScoutReport() {
    }

    public ScoutReport(String id, String playerName, String position, int potentialScore, String notes) {
        this.id = id;
        this.playerName = playerName;
        this.position = position;
        this.potentialScore = potentialScore;
        this.notes = notes;
    }

    public static ScoutReport create(String playerName, String position, int potentialScore, String notes) {
        return new ScoutReport(null, playerName, position, potentialScore, notes);
    }

    public void update(String playerName, String position, int potentialScore, String notes) {
        this.playerName = playerName;
        this.position = position;
        this.potentialScore = potentialScore;
        this.notes = notes;
    }

    public String getId() {
        return id;
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
