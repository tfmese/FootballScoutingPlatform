package com.scouting.scoutingservice.infrastructure;

import com.scouting.scoutingservice.domain.ScoutReport;
import com.scouting.scoutingservice.domain.ScoutReportRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class MongoScoutReportRepositoryAdapter implements ScoutReportRepository {

    private final SpringDataScoutReportRepository repository;

    public MongoScoutReportRepositoryAdapter(SpringDataScoutReportRepository repository) {
        this.repository = repository;
    }

    @Override
    public ScoutReport save(ScoutReport report) {
        return repository.save(report);
    }

    @Override
    public Optional<ScoutReport> findById(String id) {
        return repository.findById(id);
    }

    @Override
    public List<ScoutReport> findAll() {
        return repository.findAll();
    }

    @Override
    public void deleteById(String id) {
        repository.deleteById(id);
    }
}
