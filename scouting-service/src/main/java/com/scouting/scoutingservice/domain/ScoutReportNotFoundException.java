package com.scouting.scoutingservice.domain;

public class ScoutReportNotFoundException extends RuntimeException {
    public ScoutReportNotFoundException(String id) {
        super("Scout report not found: " + id);
    }
}
