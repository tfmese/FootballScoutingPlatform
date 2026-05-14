package com.scouting.scoutingservice.interfaces;

import com.scouting.scoutingservice.application.LinkedPlayerNotFoundException;
import com.scouting.scoutingservice.application.PlayerLookupFailedException;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
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
        String playerId = "4d6e5ef7-f545-4b72-a0f8-c4b2f66d83f0";
        ScoutReport report = new ScoutReport("1", playerId, "Arda Guler", "AM", 20, 92, 74, 88, 86, 85, 25000000L, "Sign", "Top talent");
        when(service.create(anyString(), anyString(), anyString(), anyInt(), anyInt(), anyInt(), anyInt(), anyInt(), anyLong(), anyString(), anyString())).thenReturn(report);

        mockMvc.perform(post("/scouts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"playerId\":\"" + playerId + "\",\"playerName\":\"Arda Guler\",\"position\":\"AM\",\"playerAge\":20,\"technicalScore\":92,\"physicalScore\":74,\"tacticalScore\":88,\"mentalScore\":86,\"expectedFee\":25000000,\"recommendation\":\"Sign\",\"notes\":\"Top talent\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.playerName").value("Arda Guler"));
    }

    @Test
    void getScoutByIdWhenExistsShouldReturnOk() throws Exception {
        ScoutReport report = new ScoutReport("1", null, "Kenan Yildiz", "LW", 20, 86, 84, 85, 89, 86, 18000000L, "Monitor", "High upside");
        when(service.getById(eq("1"))).thenReturn(report);

        mockMvc.perform(get("/scouts/{id}", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value("1"));
    }

    @Test
    void getScoutsShouldReturnList() throws Exception {
        when(service.getAll()).thenReturn(List.of(
                new ScoutReport("1", null, "A", "CB", 23, 70, 68, 69, 71, 70, 5000000L, "Monitor", "n1"),
                new ScoutReport("2", null, "B", "RB", 22, 71, 75, 72, 70, 72, 7500000L, "Monitor", "n2")
        ));

        mockMvc.perform(get("/scouts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.items.length()").value(2));
    }

    @Test
    void putScoutsShouldReturnUpdated() throws Exception {
        String playerId = "d2d47915-4dca-4cd3-ae14-4b346a779d9f";
        ScoutReport report = new ScoutReport("1", playerId, "Updated", "CM", 22, 81, 79, 83, 85, 82, 12000000L, "Sign", "updated");
        when(service.update(eq("1"), anyString(), anyString(), anyString(), anyInt(), anyInt(), anyInt(), anyInt(), anyInt(), anyLong(), anyString(), anyString())).thenReturn(report);

        mockMvc.perform(put("/scouts/{id}", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"playerId\":\"" + playerId + "\",\"playerName\":\"Updated\",\"position\":\"CM\",\"playerAge\":22,\"technicalScore\":81,\"physicalScore\":79,\"tacticalScore\":83,\"mentalScore\":85,\"expectedFee\":12000000,\"recommendation\":\"Sign\",\"notes\":\"updated\"}"))
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
                        .content("{\"playerName\":\"\",\"position\":\"AM\",\"playerAge\":0,\"technicalScore\":0,\"physicalScore\":0,\"tacticalScore\":0,\"mentalScore\":0,\"expectedFee\":-1,\"recommendation\":\"\",\"notes\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void postScoutsWithInvalidPlayerIdShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/scouts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"playerId\":\"not-a-valid-uuid\",\"playerName\":\"X\",\"position\":\"AM\",\"playerAge\":20,\"technicalScore\":50,\"physicalScore\":50,\"tacticalScore\":50,\"mentalScore\":50,\"expectedFee\":1000000,\"recommendation\":\"Monitor\",\"notes\":\"n\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void postScoutsWithoutPlayerIdShouldReturnCreated() throws Exception {
        ScoutReport report = new ScoutReport("1", null, "Legacy", "ST", 21, 70, 68, 66, 76, 70, 3000000L, "Monitor", "no player link");
        when(service.create(isNull(), anyString(), anyString(), anyInt(), anyInt(), anyInt(), anyInt(), anyInt(), anyLong(), anyString(), anyString())).thenReturn(report);

        mockMvc.perform(post("/scouts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"playerName\":\"Legacy\",\"position\":\"ST\",\"playerAge\":21,\"technicalScore\":70,\"physicalScore\":68,\"tacticalScore\":66,\"mentalScore\":76,\"expectedFee\":3000000,\"recommendation\":\"Monitor\",\"notes\":\"no player link\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.playerName").value("Legacy"));
    }

    @Test
    void postScoutsWithUnknownLinkedPlayerShouldReturn404() throws Exception {
        String playerId = "4d6e5ef7-f545-4b72-a0f8-c4b2f66d83f0";
        when(service.create(eq(playerId), anyString(), anyString(), anyInt(), anyInt(), anyInt(), anyInt(), anyInt(), anyLong(), anyString(), anyString()))
                .thenThrow(new LinkedPlayerNotFoundException(playerId));

        mockMvc.perform(post("/scouts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"playerId\":\"" + playerId + "\",\"playerName\":\"Arda Guler\",\"position\":\"AM\",\"playerAge\":20,\"technicalScore\":92,\"physicalScore\":74,\"tacticalScore\":88,\"mentalScore\":86,\"expectedFee\":25000000,\"recommendation\":\"Sign\",\"notes\":\"Top talent\"}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Linked player not found: " + playerId));
    }

    @Test
    void postScoutsWhenPlayerLookupFailsShouldReturn502() throws Exception {
        String playerId = "4d6e5ef7-f545-4b72-a0f8-c4b2f66d83f0";
        when(service.create(eq(playerId), anyString(), anyString(), anyInt(), anyInt(), anyInt(), anyInt(), anyInt(), anyLong(), anyString(), anyString()))
                .thenThrow(new PlayerLookupFailedException("boom"));

        mockMvc.perform(post("/scouts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"playerId\":\"" + playerId + "\",\"playerName\":\"Arda Guler\",\"position\":\"AM\",\"playerAge\":20,\"technicalScore\":92,\"physicalScore\":74,\"tacticalScore\":88,\"mentalScore\":86,\"expectedFee\":25000000,\"recommendation\":\"Sign\",\"notes\":\"Top talent\"}"))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.message").value("Player service integration failed"));
    }
}
