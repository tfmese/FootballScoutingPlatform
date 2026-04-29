package com.scouting.scoutingservice.interfaces;

import com.scouting.scoutingservice.application.ScoutReportService;
import com.scouting.scoutingservice.domain.ScoutReport;
import com.scouting.scoutingservice.domain.ScoutReportNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ScoutReportControllerTest {

    private ScoutReportService service;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        service = mock(ScoutReportService.class);
        ScoutReportController controller = new ScoutReportController(service);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void postScoutsShouldReturnCreated() throws Exception {
        ScoutReport report = new ScoutReport("1", "Arda Guler", "AM", 90, "Top talent");
        when(service.create(anyString(), anyString(), anyInt(), anyString())).thenReturn(report);

        mockMvc.perform(post("/scouts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"playerName\":\"Arda Guler\",\"position\":\"AM\",\"potentialScore\":90,\"notes\":\"Top talent\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.playerName").value("Arda Guler"));
    }

    @Test
    void getScoutByIdWhenExistsShouldReturnOk() throws Exception {
        ScoutReport report = new ScoutReport("1", "Kenan Yildiz", "LW", 86, "High upside");
        when(service.getById(eq("1"))).thenReturn(report);

        mockMvc.perform(get("/scouts/{id}", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value("1"));
    }

    @Test
    void getScoutsShouldReturnList() throws Exception {
        when(service.getAll()).thenReturn(List.of(
                new ScoutReport("1", "A", "CB", 70, "n1"),
                new ScoutReport("2", "B", "RB", 71, "n2")
        ));

        mockMvc.perform(get("/scouts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    void putScoutsShouldReturnUpdated() throws Exception {
        ScoutReport report = new ScoutReport("1", "Updated", "CM", 82, "updated");
        when(service.update(eq("1"), anyString(), anyString(), anyInt(), anyString())).thenReturn(report);

        mockMvc.perform(put("/scouts/{id}", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"playerName\":\"Updated\",\"position\":\"CM\",\"potentialScore\":82,\"notes\":\"updated\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.playerName").value("Updated"));
    }

    @Test
    void deleteScoutsShouldReturnNoContent() throws Exception {
        doNothing().when(service).delete(eq("1"));

        mockMvc.perform(delete("/scouts/{id}", "1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void getScoutByIdWhenNotFoundShouldReturn404() throws Exception {
        when(service.getById(eq("404"))).thenThrow(new ScoutReportNotFoundException("404"));

        mockMvc.perform(get("/scouts/{id}", "404"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Scout report not found: 404"));
    }

    @Test
    void postScoutsWithInvalidBodyShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/scouts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"playerName\":\"\",\"position\":\"AM\",\"potentialScore\":0,\"notes\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }
}
