package com.scouting.scoutingservice.application;

import com.scouting.scoutingservice.application.port.PlayerLookupPort;
import com.scouting.scoutingservice.application.port.PlayerSnapshot;
import com.scouting.scoutingservice.domain.ScoutReport;
import com.scouting.scoutingservice.domain.ScoutReportNotFoundException;
import com.scouting.scoutingservice.domain.ScoutReportRepository;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class ScoutReportService {

    private final ScoutReportRepository repository;
    private final PlayerLookupPort playerLookupPort;

    public ScoutReportService(ScoutReportRepository repository, PlayerLookupPort playerLookupPort) {
        this.repository = repository;
        this.playerLookupPort = playerLookupPort;
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
        PlayerSnapshot player = resolvePlayerSnapshot(playerId, playerName, position, playerAge);
        int potentialScore = calculatePotentialScore(technicalScore, physicalScore, tacticalScore, mentalScore);
        return repository.save(ScoutReport.builder()
                .playerId(player.id())
                .playerName(player.name())
                .position(player.position())
                .playerAge(player.age())
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
        PlayerSnapshot player = resolvePlayerSnapshot(playerId, playerName, position, playerAge);
        int potentialScore = calculatePotentialScore(technicalScore, physicalScore, tacticalScore, mentalScore);
        report.update(
                player.id(),
                player.name(),
                player.position(),
                player.age(),
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

    private PlayerSnapshot resolvePlayerSnapshot(String playerId, String playerName, String position, int playerAge) {
        if (playerId == null || playerId.isBlank()) {
            return new PlayerSnapshot(null, playerName, position, playerAge);
        }

        return playerLookupPort.findById(playerId)
                .orElseThrow(() -> new LinkedPlayerNotFoundException(playerId));
    }
}
