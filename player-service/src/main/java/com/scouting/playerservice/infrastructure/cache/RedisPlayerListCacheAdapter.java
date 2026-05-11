package com.scouting.playerservice.infrastructure.cache;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scouting.playerservice.application.port.PlayerListCachePort;
import com.scouting.playerservice.domain.Player;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

public final class RedisPlayerListCacheAdapter implements PlayerListCachePort {

    static final String ALL_PLAYERS_KEY = "player-service:players:all";

    private final StringRedisTemplate redis;
    private final ObjectMapper objectMapper;
    private final long ttlSeconds;

    public RedisPlayerListCacheAdapter(StringRedisTemplate redis, ObjectMapper objectMapper, long ttlSeconds) {
        this.redis = redis;
        this.objectMapper = objectMapper;
        this.ttlSeconds = ttlSeconds;
    }

    @Override
    public Optional<List<Player>> getCachedPlayerList() {
        String json = redis.opsForValue().get(ALL_PLAYERS_KEY);
        if (json == null || json.isBlank()) {
            return Optional.empty();
        }
        try {
            List<Player> list = objectMapper.readValue(json, new TypeReference<>() {
            });
            return Optional.of(list);
        } catch (Exception ex) {
            return Optional.empty();
        }
    }

    @Override
    public void putCachedPlayerList(List<Player> players) {
        try {
            String json = objectMapper.writeValueAsString(players);
            if (ttlSeconds > 0) {
                redis.opsForValue().set(ALL_PLAYERS_KEY, json, Duration.ofSeconds(ttlSeconds));
            } else {
                redis.opsForValue().set(ALL_PLAYERS_KEY, json);
            }
        } catch (Exception ignored) {
            // önbellek yazılamazsa sessizce yoksay; bir sonraki okuma DB'den gelir
        }
    }

    @Override
    public void evictCachedPlayerList() {
        redis.delete(ALL_PLAYERS_KEY);
    }
}
