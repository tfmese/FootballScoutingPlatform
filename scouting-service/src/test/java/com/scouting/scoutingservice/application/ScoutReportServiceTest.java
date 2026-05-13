package com.scouting.scoutingservice.application;

import com.scouting.scoutingservice.domain.ScoutReport;
import com.scouting.scoutingservice.domain.ScoutReportNotFoundException;
import com.scouting.scoutingservice.domain.ScoutReportRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ScoutReportServiceTest {

    private ScoutReportService service;

    @BeforeEach
    void setUp() {
        service = new ScoutReportService(new InMemoryScoutReportRepository());
    }

    @Test
    void createShouldGenerateId() {
        String playerId = "4d6e5ef7-f545-4b72-a0f8-c4b2f66d83f0";
        ScoutReport created = service.create(playerId, "Arda Guler", "AM", 20, 94, 77, 90, 88, 25000000L, "Sign", "Excellent creativity");

        assertNotNull(created.getId());
        assertEquals("Arda Guler", created.getPlayerName());
        assertEquals(playerId, created.getPlayerId());
        assertEquals(20, created.getPlayerAge());
        assertEquals(25000000L, created.getExpectedFee());
        assertEquals(87, created.getPotentialScore());
    }

    @Test
    void getByIdWhenExistsShouldReturnReport() {
        ScoutReport created = service.create(null, "Kenan Yildiz", "LW", 20, 91, 72, 83, 78, 18000000L, "Monitor", "Strong dribbling");

        ScoutReport found = service.getById(created.getId());

        assertEquals(created.getId(), found.getId());
    }

    @Test
    void updateShouldPersistChanges() {
        ScoutReport created = service.create(null, "Old Name", "CM", 21, 70, 68, 69, 71, 4000000L, "Monitor", "Old");

        String playerId = "d2d47915-4dca-4cd3-ae14-4b346a779d9f";
        ScoutReport updated = service.update(created.getId(), playerId, "New Name", "CDM", 22, 82, 79, 76, 81, 12000000L, "Sign", "Updated");

        assertEquals("New Name", updated.getPlayerName());
        assertEquals(80, updated.getPotentialScore());
        assertEquals(playerId, updated.getPlayerId());
        assertEquals("Sign", updated.getRecommendation());
        assertEquals(12000000L, updated.getExpectedFee());
    }

    @Test
    void deleteShouldRemoveReport() {
        ScoutReport created = service.create(null, "Delete", "CB", 24, 66, 64, 62, 68, 3000000L, "Reject", "To be removed");

        service.delete(created.getId());

        assertThrows(ScoutReportNotFoundException.class, () -> service.getById(created.getId()));
    }

    @Test
    void getAllShouldReturnAllReports() {
        service.create(null, "One", "RB", 23, 75, 71, 70, 72, 6500000L, "Monitor", "n1");
        service.create(null, "Two", "LB", 22, 79, 81, 76, 78, 11000000L, "Monitor", "n2");

        List<ScoutReport> reports = service.getAll();

        assertEquals(2, reports.size());
    }

    private static final class InMemoryScoutReportRepository implements ScoutReportRepository {
        private final Map<String, ScoutReport> storage = new HashMap<>();
        private int idSeed = 0;

        @Override
        public ScoutReport save(ScoutReport report) {
            String id = report.getId();
            if (id == null) {
                id = String.valueOf(++idSeed);
                report = new ScoutReport(
                        id,
                        report.getPlayerId(),
                        report.getPlayerName(),
                        report.getPosition(),
                        report.getPlayerAge(),
                        report.getTechnicalScore(),
                        report.getPhysicalScore(),
                        report.getTacticalScore(),
                        report.getMentalScore(),
                        report.getPotentialScore(),
                        report.getExpectedFee(),
                        report.getRecommendation(),
                        report.getNotes()
                );
            }
            storage.put(id, report);
            return report;
        }

        @Override
        public Optional<ScoutReport> findById(String id) {
            return Optional.ofNullable(storage.get(id));
        }

        @Override
        public List<ScoutReport> findAll() {
            return new ArrayList<>(storage.values());
        }

        @Override
        public void deleteById(String id) {
            storage.remove(id);
        }
    }
}
