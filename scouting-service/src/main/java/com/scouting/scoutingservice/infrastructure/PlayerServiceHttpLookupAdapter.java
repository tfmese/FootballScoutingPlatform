package com.scouting.scoutingservice.infrastructure;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scouting.scoutingservice.application.PlayerLookupFailedException;
import com.scouting.scoutingservice.application.port.PlayerLookupPort;
import com.scouting.scoutingservice.application.port.PlayerSnapshot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

@Repository
public class PlayerServiceHttpLookupAdapter implements PlayerLookupPort {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String playerServiceBaseUrl;

    @Autowired
    public PlayerServiceHttpLookupAdapter(
            @Value("${integration.player-service.base-url:http://localhost:8081}") String playerServiceBaseUrl
    ) {
        this(playerServiceBaseUrl, HttpClient.newHttpClient(), new ObjectMapper());
    }

    PlayerServiceHttpLookupAdapter(
            String playerServiceBaseUrl,
            HttpClient httpClient,
            ObjectMapper objectMapper
    ) {
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
        this.playerServiceBaseUrl = normalize(playerServiceBaseUrl);
    }

    @Override
    public Optional<PlayerSnapshot> findById(String playerId) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(playerServiceBaseUrl + "/api/players/" + playerId))
                .GET()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 404) {
                return Optional.empty();
            }
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new PlayerLookupFailedException("Player lookup failed with HTTP " + response.statusCode());
            }

            JsonNode root = objectMapper.readTree(response.body());
            JsonNode data = root.path("data");
            if (data.isMissingNode() || data.isNull()) {
                throw new PlayerLookupFailedException("Player lookup response did not include data");
            }

            return Optional.of(new PlayerSnapshot(
                    data.path("id").asText(playerId),
                    data.path("name").asText(""),
                    data.path("position").asText(""),
                    data.path("age").asInt(0)
            ));
        } catch (IOException | InterruptedException exception) {
            if (exception instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            throw new PlayerLookupFailedException("Player lookup request failed", exception);
        }
    }

    private static String normalize(String baseUrl) {
        if (baseUrl.endsWith("/")) {
            return baseUrl.substring(0, baseUrl.length() - 1);
        }
        return baseUrl;
    }
}
