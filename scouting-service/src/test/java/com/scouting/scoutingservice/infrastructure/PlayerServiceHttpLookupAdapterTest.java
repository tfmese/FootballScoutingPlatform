package com.scouting.scoutingservice.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scouting.scoutingservice.application.PlayerLookupFailedException;
import com.scouting.scoutingservice.application.port.PlayerSnapshot;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PlayerServiceHttpLookupAdapterTest {

    @Test
    void findByIdWhenPlayerServiceReturns404ShouldReturnEmpty() throws Exception {
        HttpClient httpClient = mock(HttpClient.class);
        HttpResponse<String> response = mock(HttpResponse.class);
        when(response.statusCode()).thenReturn(404);
        when(httpClient.send(any(), any(HttpResponse.BodyHandler.class))).thenReturn(response);

        PlayerServiceHttpLookupAdapter adapter = new PlayerServiceHttpLookupAdapter(
                "http://player-service",
                httpClient,
                new ObjectMapper()
        );

        Optional<PlayerSnapshot> result = adapter.findById("player-404");

        assertTrue(result.isEmpty());
    }

    @Test
    void findByIdWhenPlayerServiceReturnsSuccessShouldMapResponseData() throws Exception {
        HttpClient httpClient = mock(HttpClient.class);
        HttpResponse<String> response = mock(HttpResponse.class);
        when(response.statusCode()).thenReturn(200);
        when(response.body()).thenReturn("""
                {"data":{"id":"player-1","name":"Arda Guler","position":"AM","age":20}}
                """);
        when(httpClient.send(any(), any(HttpResponse.BodyHandler.class))).thenReturn(response);

        PlayerServiceHttpLookupAdapter adapter = new PlayerServiceHttpLookupAdapter(
                "http://player-service/",
                httpClient,
                new ObjectMapper()
        );

        PlayerSnapshot snapshot = adapter.findById("player-1").orElseThrow();

        assertEquals("player-1", snapshot.id());
        assertEquals("Arda Guler", snapshot.name());
        assertEquals("AM", snapshot.position());
        assertEquals(20, snapshot.age());
    }

    @Test
    void findByIdWhenPlayerServiceReturnsServerErrorShouldThrowLookupFailed() throws Exception {
        HttpClient httpClient = mock(HttpClient.class);
        HttpResponse<String> response = mock(HttpResponse.class);
        when(response.statusCode()).thenReturn(502);
        when(httpClient.send(any(), any(HttpResponse.BodyHandler.class))).thenReturn(response);

        PlayerServiceHttpLookupAdapter adapter = new PlayerServiceHttpLookupAdapter(
                "http://player-service",
                httpClient,
                new ObjectMapper()
        );

        PlayerLookupFailedException exception = assertThrows(
                PlayerLookupFailedException.class,
                () -> adapter.findById("player-502")
        );

        assertTrue(exception.getMessage().contains("HTTP 502"));
    }

    @Test
    void findByIdWhenSuccessfulResponseHasNoDataShouldThrowLookupFailed() throws Exception {
        HttpClient httpClient = mock(HttpClient.class);
        HttpResponse<String> response = mock(HttpResponse.class);
        when(response.statusCode()).thenReturn(200);
        when(response.body()).thenReturn("{\"message\":\"ok\"}");
        when(httpClient.send(any(), any(HttpResponse.BodyHandler.class))).thenReturn(response);

        PlayerServiceHttpLookupAdapter adapter = new PlayerServiceHttpLookupAdapter(
                "http://player-service",
                httpClient,
                new ObjectMapper()
        );

        PlayerLookupFailedException exception = assertThrows(
                PlayerLookupFailedException.class,
                () -> adapter.findById("player-no-data")
        );

        assertTrue(exception.getMessage().contains("did not include data"));
    }

    @Test
    void findByIdWhenHttpClientThrowsShouldWrapAsLookupFailed() throws Exception {
        HttpClient httpClient = mock(HttpClient.class);
        when(httpClient.send(any(), any(HttpResponse.BodyHandler.class))).thenThrow(new IOException("connection reset"));

        PlayerServiceHttpLookupAdapter adapter = new PlayerServiceHttpLookupAdapter(
                "http://player-service",
                httpClient,
                new ObjectMapper()
        );

        PlayerLookupFailedException exception = assertThrows(
                PlayerLookupFailedException.class,
                () -> adapter.findById("player-io")
        );

        assertTrue(exception.getMessage().contains("request failed"));
    }

    @Test
    void findByIdWhenHttpClientIsInterruptedShouldRestoreInterruptFlag() throws Exception {
        HttpClient httpClient = mock(HttpClient.class);
        when(httpClient.send(any(), any(HttpResponse.BodyHandler.class))).thenThrow(new InterruptedException("interrupted"));

        PlayerServiceHttpLookupAdapter adapter = new PlayerServiceHttpLookupAdapter(
                "http://player-service",
                httpClient,
                new ObjectMapper()
        );

        try {
            assertThrows(PlayerLookupFailedException.class, () -> adapter.findById("player-interrupted"));
            assertTrue(Thread.currentThread().isInterrupted());
        } finally {
            assertTrue(Thread.interrupted());
        }
    }
}
