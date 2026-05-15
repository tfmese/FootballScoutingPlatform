package com.scouting.playerservice.infrastructure.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scouting.playerservice.application.port.PlayerListCachePort;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
@EnableConfigurationProperties(RedisPlayerCacheProperties.class)
public class PlayerListCacheConfiguration {

    @Bean
    @ConditionalOnProperty(prefix = "player.cache.redis", name = "enabled", havingValue = "true")
    public LettuceConnectionFactory playerListRedisConnectionFactory(RedisPlayerCacheProperties props) {
        RedisStandaloneConfiguration standalone = new RedisStandaloneConfiguration();
        standalone.setHostName(props.getHost());
        standalone.setPort(props.getPort());
        LettuceConnectionFactory factory = new LettuceConnectionFactory(standalone);
        return factory;
    }

    @Bean
    @ConditionalOnProperty(prefix = "player.cache.redis", name = "enabled", havingValue = "true")
    public StringRedisTemplate playerListStringRedisTemplate(LettuceConnectionFactory playerListRedisConnectionFactory) {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(playerListRedisConnectionFactory);
        return template;
    }

    @Bean
    @ConditionalOnProperty(prefix = "player.cache.redis", name = "enabled", havingValue = "true")
    public PlayerListCachePort redisPlayerListCachePort(
            StringRedisTemplate playerListStringRedisTemplate,
            RedisPlayerCacheProperties props
    ) {
        ObjectMapper objectMapper = new ObjectMapper();
        return new RedisPlayerListCacheAdapter(playerListStringRedisTemplate, objectMapper, props.getTtlSeconds());
    }

    @Bean
    @ConditionalOnProperty(prefix = "player.cache.redis", name = "enabled", havingValue = "false", matchIfMissing = true)
    public PlayerListCachePort noopPlayerListCachePort() {
        return new NoOpPlayerListCacheAdapter();
    }
}
