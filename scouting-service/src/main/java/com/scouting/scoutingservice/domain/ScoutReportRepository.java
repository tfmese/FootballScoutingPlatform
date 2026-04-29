package com.scouting.scoutingservice.domain;

import java.util.List;
import java.util.Optional;

public interface ScoutReportRepository {
    ScoutReport save(ScoutReport report);

    Optional<ScoutReport> findById(String id);

    List<ScoutReport> findAll();

    void deleteById(String id);
}
