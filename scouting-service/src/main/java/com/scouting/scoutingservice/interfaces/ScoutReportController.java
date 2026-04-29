package com.scouting.scoutingservice.interfaces;

import com.scouting.scoutingservice.application.ScoutReportService;
import com.scouting.scoutingservice.domain.ScoutReport;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/scouts")
public class ScoutReportController {

    private final ScoutReportService service;

    public ScoutReportController(ScoutReportService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ScoutReport>> create(@Valid @RequestBody CreateScoutReportRequest request) {
        ScoutReport created = service.create(
                request.playerName(),
                request.position(),
                request.potentialScore(),
                request.notes()
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>("Scout report created", created));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ScoutReport>> getById(@PathVariable String id) {
        return ResponseEntity.ok(new ApiResponse<>("Scout report retrieved", service.getById(id)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ScoutReport>>> getAll() {
        return ResponseEntity.ok(new ApiResponse<>("Scout reports retrieved", service.getAll()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ScoutReport>> update(
            @PathVariable String id,
            @Valid @RequestBody UpdateScoutReportRequest request
    ) {
        ScoutReport updated = service.update(
                id,
                request.playerName(),
                request.position(),
                request.potentialScore(),
                request.notes()
        );
        return ResponseEntity.ok(new ApiResponse<>("Scout report updated", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
