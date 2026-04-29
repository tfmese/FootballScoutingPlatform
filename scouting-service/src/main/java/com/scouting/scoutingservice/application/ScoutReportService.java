package com.scouting.scoutingservice.application;

import com.scouting.scoutingservice.domain.ScoutReport;
import com.scouting.scoutingservice.domain.ScoutReportNotFoundException;
import com.scouting.scoutingservice.domain.ScoutReportRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScoutReportService {

    private final ScoutReportRepository repository;

    public ScoutReportService(ScoutReportRepository repository) {
        this.repository = repository;
    }

    public ScoutReport create(String playerName, String position, int potentialScore, String notes) {
        return repository.save(ScoutReport.create(playerName, position, potentialScore, notes));
    }

    public ScoutReport getById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new ScoutReportNotFoundException(id));
    }

    public List<ScoutReport> getAll() {
        return repository.findAll();
    }

    public ScoutReport update(String id, String playerName, String position, int potentialScore, String notes) {
        ScoutReport report = getById(id);
        report.update(playerName, position, potentialScore, notes);
        return repository.save(report);
    }

    public void delete(String id) {
        getById(id);
        repository.deleteById(id);
    }
}
