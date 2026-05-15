package com.scouting.desktopgui.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scouting.desktopgui.model.Player;
import com.scouting.desktopgui.model.ScoutReport;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ScoutingApiClient {
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String gatewayBaseUrl;
    private final String playerServiceBaseUrl;
    private final String scoutingServiceBaseUrl;
    private final boolean useGateway;

    public ScoutingApiClient(String gatewayBaseUrl, String playerServiceBaseUrl, String scoutingServiceBaseUrl, boolean useGateway) {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
        this.gatewayBaseUrl = normalize(gatewayBaseUrl);
        this.playerServiceBaseUrl = normalize(playerServiceBaseUrl);
        this.scoutingServiceBaseUrl = normalize(scoutingServiceBaseUrl);
        this.useGateway = useGateway;
    }

    public List<Player> getPlayers() throws IOException, InterruptedException {
        HttpResponse<String> response = sendGet(resolvePlayersUrl(""));
        JsonNode root = parseRoot(response);
        JsonNode data = root.get("data");
        JsonNode items = data != null && data.has("items") ? data.get("items") : data;
        return objectMapper.convertValue(items, new TypeReference<List<Player>>() {
        });
    }

    public void createPlayer(String name, String position, int age, String club, String preferredFoot) throws IOException, InterruptedException {
        sendWrite(resolvePlayersUrl(""), "POST", Map.of(
                "name", name,
                "position", position,
                "age", age,
                "club", club,
                "preferredFoot", preferredFoot
        ));
    }

    public void updatePlayer(String id, String name, String position, int age, String club, String preferredFoot) throws IOException, InterruptedException {
        sendWrite(resolvePlayersUrl("/" + id), "PUT", Map.of(
                "name", name,
                "position", position,
                "age", age,
                "club", club,
                "preferredFoot", preferredFoot
        ));
    }

    public void deletePlayer(String id) throws IOException, InterruptedException {
        sendWithoutBody(resolvePlayersUrl("/" + id), "DELETE");
    }

    public List<ScoutReport> getScoutReports() throws IOException, InterruptedException {
        HttpResponse<String> response = sendGet(resolveScoutsUrl(""));
        JsonNode root = parseRoot(response);
        JsonNode data = root.get("data");
        JsonNode items = data != null && data.has("items") ? data.get("items") : data;
        return objectMapper.convertValue(items, new TypeReference<List<ScoutReport>>() {
        });
    }

    public void createScoutReport(
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
    ) throws IOException, InterruptedException {
        Map<String, Object> body = new LinkedHashMap<>();
        if (playerId != null && !playerId.isBlank()) {
            body.put("playerId", playerId);
        }
        body.put("playerName", playerName);
        body.put("position", position);
        body.put("playerAge", playerAge);
        body.put("technicalScore", technicalScore);
        body.put("physicalScore", physicalScore);
        body.put("tacticalScore", tacticalScore);
        body.put("mentalScore", mentalScore);
        body.put("expectedFee", expectedFee);
        body.put("recommendation", recommendation);
        body.put("notes", notes);
        sendWrite(resolveScoutsUrl(""), "POST", body);
    }

    public void updateScoutReport(
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
    ) throws IOException, InterruptedException {
        Map<String, Object> body = new LinkedHashMap<>();
        if (playerId != null && !playerId.isBlank()) {
            body.put("playerId", playerId);
        }
        body.put("playerName", playerName);
        body.put("position", position);
        body.put("playerAge", playerAge);
        body.put("technicalScore", technicalScore);
        body.put("physicalScore", physicalScore);
        body.put("tacticalScore", tacticalScore);
        body.put("mentalScore", mentalScore);
        body.put("expectedFee", expectedFee);
        body.put("recommendation", recommendation);
        body.put("notes", notes);
        sendWrite(resolveScoutsUrl("/" + id), "PUT", body);
    }

    public void deleteScoutReport(String id) throws IOException, InterruptedException {
        sendWithoutBody(resolveScoutsUrl("/" + id), "DELETE");
    }

    private HttpResponse<String> sendGet(String url) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        ensureSuccess(response);
        return response;
    }

    private void sendWithoutBody(String url, String method) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .method(method, HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        ensureSuccess(response);
    }

    private void sendWrite(String url, String method, Map<String, Object> body) throws IOException, InterruptedException {
        String requestBody = objectMapper.writeValueAsString(body);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .method(method, HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        ensureSuccess(response);
    }

    private JsonNode parseRoot(HttpResponse<String> response) throws IOException {
        return objectMapper.readTree(response.body());
    }

    private void ensureSuccess(HttpResponse<String> response) throws IOException {
        int status = response.statusCode();
        if (status >= 200 && status < 300) {
            return;
        }

        String message = "HTTP " + status;
        try {
            JsonNode root = objectMapper.readTree(response.body());
            if (root.has("message")) {
                message = root.get("message").asText();
            }
        } catch (Exception ignored) {

        }
        throw new IOException(message);
    }

    private String normalize(String url) {
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }

    private String resolvePlayersUrl(String suffix) {
        if (useGateway) {
            return gatewayBaseUrl + "/api/players" + suffix;
        }
        return playerServiceBaseUrl + "/api/players" + suffix;
    }

    private String resolveScoutsUrl(String suffix) {
        if (useGateway) {
            return gatewayBaseUrl + "/api/scouts" + suffix;
        }
        return scoutingServiceBaseUrl + "/api/scouts" + suffix;
    }
}
