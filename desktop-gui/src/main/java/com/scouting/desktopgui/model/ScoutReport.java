package com.scouting.desktopgui.model;

public class ScoutReport {
    private String id;
    private String playerId;
    private String playerName;
    private String position;
    private int playerAge;
    private int technicalScore;
    private int physicalScore;
    private int tacticalScore;
    private int mentalScore;
    private int potentialScore;
    private long expectedFee;
    private String recommendation;
    private String notes;

    public String getId() {
        return id;
    }

    public String getPlayerId() {
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

    public int getTechnicalScore() {
        return technicalScore;
    }

    public int getPlayerAge() {
        return playerAge;
    }

    public int getPhysicalScore() {
        return physicalScore;
    }

    public int getTacticalScore() {
        return tacticalScore;
    }

    public int getMentalScore() {
        return mentalScore;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public long getExpectedFee() {
        return expectedFee;
    }

    public String getNotes() {
        return notes;
    }
}
