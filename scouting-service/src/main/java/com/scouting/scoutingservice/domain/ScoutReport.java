package com.scouting.scoutingservice.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "scout_reports")
public class ScoutReport {
    @Id
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

    protected ScoutReport() {
    }

    public ScoutReport(String id, String playerName, String position, int potentialScore, String notes) {
        this(id, null, playerName, position, 18, potentialScore, potentialScore, potentialScore, potentialScore, potentialScore, 0L, "Monitor", notes);
    }

    public ScoutReport(
            String id,
            String playerId,
            String playerName,
            String position,
            int playerAge,
            int technicalScore,
            int physicalScore,
            int tacticalScore,
            int mentalScore,
            int potentialScore,
            long expectedFee,
            String recommendation,
            String notes
    ) {
        this.id = id;
        this.playerId = playerId;
        this.playerName = playerName;
        this.position = position;
        this.playerAge = playerAge;
        this.technicalScore = technicalScore;
        this.physicalScore = physicalScore;
        this.tacticalScore = tacticalScore;
        this.mentalScore = mentalScore;
        this.potentialScore = potentialScore;
        this.expectedFee = expectedFee;
        this.recommendation = recommendation;
        this.notes = notes;
    }

    /**
     * Yeni rapor oluşturma için Builder.
     */
    public static Builder builder() {
        return new Builder();
    }

    public void update(
            String playerId,
            String playerName,
            String position,
            int playerAge,
            int technicalScore,
            int physicalScore,
            int tacticalScore,
            int mentalScore,
            int potentialScore,
            long expectedFee,
            String recommendation,
            String notes
    ) {
        this.playerId = playerId;
        this.playerName = playerName;
        this.position = position;
        this.playerAge = playerAge;
        this.technicalScore = technicalScore;
        this.physicalScore = physicalScore;
        this.tacticalScore = tacticalScore;
        this.mentalScore = mentalScore;
        this.potentialScore = potentialScore;
        this.expectedFee = expectedFee;
        this.recommendation = recommendation;
        this.notes = notes;
    }

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

    public int getPlayerAge() {
        return playerAge;
    }

    public int getTechnicalScore() {
        return technicalScore;
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

    public int getPotentialScore() {
        return potentialScore;
    }

    public long getExpectedFee() {
        return expectedFee;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public String getNotes() {
        return notes;
    }

    public static final class Builder {
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

        public Builder playerId(String playerId) {
            this.playerId = playerId;
            return this;
        }

        public Builder playerName(String playerName) {
            this.playerName = playerName;
            return this;
        }

        public Builder position(String position) {
            this.position = position;
            return this;
        }

        public Builder playerAge(int playerAge) {
            this.playerAge = playerAge;
            return this;
        }

        public Builder technicalScore(int technicalScore) {
            this.technicalScore = technicalScore;
            return this;
        }

        public Builder physicalScore(int physicalScore) {
            this.physicalScore = physicalScore;
            return this;
        }

        public Builder tacticalScore(int tacticalScore) {
            this.tacticalScore = tacticalScore;
            return this;
        }

        public Builder mentalScore(int mentalScore) {
            this.mentalScore = mentalScore;
            return this;
        }

        public Builder potentialScore(int potentialScore) {
            this.potentialScore = potentialScore;
            return this;
        }

        public Builder expectedFee(long expectedFee) {
            this.expectedFee = expectedFee;
            return this;
        }

        public Builder recommendation(String recommendation) {
            this.recommendation = recommendation;
            return this;
        }

        public Builder notes(String notes) {
            this.notes = notes;
            return this;
        }

        public ScoutReport build() {
            return new ScoutReport(
                    null,
                    playerId,
                    playerName,
                    position,
                    playerAge,
                    technicalScore,
                    physicalScore,
                    tacticalScore,
                    mentalScore,
                    potentialScore,
                    expectedFee,
                    recommendation,
                    notes
            );
        }
    }
}
