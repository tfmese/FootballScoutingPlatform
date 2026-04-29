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
        return objectMapper.convertValue(root.get("data"), new TypeReference<>() {
        });
    }

    public void createPlayer(String name, String position, int age) throws IOException, InterruptedException {
        sendWrite(resolvePlayersUrl(""), "POST", Map.of(
                "name", name,
                "position", position,
                "age", age
        ));
    }

    public void updatePlayer(String id, String name, String position, int age) throws IOException, InterruptedException {
        sendWrite(resolvePlayersUrl("/" + id), "PUT", Map.of(
                "name", name,
                "position", position,
                "age", age
        ));
    }

    public void deletePlayer(String id) throws IOException, InterruptedException {
        sendWithoutBody(resolvePlayersUrl("/" + id), "DELETE");
    }

    public List<ScoutReport> getScoutReports() throws IOException, InterruptedException {
        HttpResponse<String> response = sendGet(resolveScoutsUrl(""));
        JsonNode root = parseRoot(response);
        return objectMapper.convertValue(root.get("data"), new TypeReference<>() {
        });
    }

    public void createScoutReport(String playerName, String position, int potentialScore, String notes) throws IOException, InterruptedException {
        sendWrite(resolveScoutsUrl(""), "POST", Map.of(
                "playerName", playerName,
                "position", position,
                "potentialScore", potentialScore,
                "notes", notes
        ));
    }

    public void updateScoutReport(String id, String playerName, String position, int potentialScore, String notes) throws IOException, InterruptedException {
        sendWrite(resolveScoutsUrl("/" + id), "PUT", Map.of(
                "playerName", playerName,
                "position", position,
                "potentialScore", potentialScore,
                "notes", notes
        ));
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
            // keep fallback message
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
        return playerServiceBaseUrl + "/players" + suffix;
    }

    private String resolveScoutsUrl(String suffix) {
        if (useGateway) {
            return gatewayBaseUrl + "/api/scouts" + suffix;
        }
        return scoutingServiceBaseUrl + "/scouts" + suffix;
    }
}
