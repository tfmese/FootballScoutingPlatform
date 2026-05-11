package com.scouting.playerservice.infrastructure.cache;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "player.cache.redis")
public class RedisPlayerCacheProperties {

    /**
     * true olduğunda Redis bağlantısı kurulur ve liste önbelleği kullanılır.
     */
    private boolean enabled = false;
    private String host = "localhost";
    private int port = 6379;
    private long ttlSeconds = 60;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public long getTtlSeconds() {
        return ttlSeconds;
    }

    public void setTtlSeconds(long ttlSeconds) {
        this.ttlSeconds = ttlSeconds;
    }
}
