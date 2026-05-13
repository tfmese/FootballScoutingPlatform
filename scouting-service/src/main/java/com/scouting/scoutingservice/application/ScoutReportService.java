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

    public ScoutReport create(
            String playerId,
            String playerName,
            String position,
            int playerAge,
            int technicalScore,
            int physicalScore,
            int tacticalScore,
            int mentalScore,
            long expectedFee,
            String recommendation,
            String notes
    ) {
        int potentialScore = calculatePotentialScore(technicalScore, physicalScore, tacticalScore, mentalScore);
        return repository.save(ScoutReport.builder()
                .playerId(playerId)
                .playerName(playerName)
                .position(position)
                .playerAge(playerAge)
                .technicalScore(technicalScore)
                .physicalScore(physicalScore)
                .tacticalScore(tacticalScore)
                .mentalScore(mentalScore)
                .potentialScore(potentialScore)
                .expectedFee(expectedFee)
                .recommendation(recommendation)
                .notes(notes)
                .build());
    }

    public ScoutReport getById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new ScoutReportNotFoundException(id));
    }

    public List<ScoutReport> getAll() {
        return repository.findAll();
    }

    public ScoutReport update(
            String id,
            String playerId,
            String playerName,
            String position,
            int playerAge,
            int technicalScore,
            int physicalScore,
            int tacticalScore,
            int mentalScore,
            long expectedFee,
            String recommendation,
            String notes
    ) {
        ScoutReport report = getById(id);
        int potentialScore = calculatePotentialScore(technicalScore, physicalScore, tacticalScore, mentalScore);
        report.update(
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
        return repository.save(report);
    }

    public void delete(String id) {
        getById(id);
        repository.deleteById(id);
    }

    private int calculatePotentialScore(int technicalScore, int physicalScore, int tacticalScore, int mentalScore) {
        return Math.round((technicalScore + physicalScore + tacticalScore + mentalScore) / 4.0f);
    }
}
