package com.scouting.scoutingservice.infrastructure;

import com.scouting.scoutingservice.domain.ScoutReport;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SpringDataScoutReportRepository extends MongoRepository<ScoutReport, String> {
}
