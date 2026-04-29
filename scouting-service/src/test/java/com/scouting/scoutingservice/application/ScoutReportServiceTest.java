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
        ScoutReport created = service.create("Arda Guler", "AM", 92, "Excellent creativity");

        assertNotNull(created.getId());
        assertEquals("Arda Guler", created.getPlayerName());
    }

    @Test
    void getByIdWhenExistsShouldReturnReport() {
        ScoutReport created = service.create("Kenan Yildiz", "LW", 88, "Strong dribbling");

        ScoutReport found = service.getById(created.getId());

        assertEquals(created.getId(), found.getId());
    }

    @Test
    void updateShouldPersistChanges() {
        ScoutReport created = service.create("Old Name", "CM", 70, "Old");

        ScoutReport updated = service.update(created.getId(), "New Name", "CDM", 78, "Updated");

        assertEquals("New Name", updated.getPlayerName());
        assertEquals(78, updated.getPotentialScore());
    }

    @Test
    void deleteShouldRemoveReport() {
        ScoutReport created = service.create("Delete", "CB", 66, "To be removed");

        service.delete(created.getId());

        assertThrows(ScoutReportNotFoundException.class, () -> service.getById(created.getId()));
    }

    @Test
    void getAllShouldReturnAllReports() {
        service.create("One", "RB", 75, "n1");
        service.create("Two", "LB", 79, "n2");

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
                report = new ScoutReport(id, report.getPlayerName(), report.getPosition(), report.getPotentialScore(), report.getNotes());
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
