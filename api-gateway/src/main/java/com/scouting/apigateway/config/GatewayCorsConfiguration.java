package com.scouting.apigateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Tarayıcıdan gateway üzerinden API çağrıları için CORS (tek sorumluluk).
 */
@Configuration
public class GatewayCorsConfiguration implements WebMvcConfigurer {

    @Value("${gateway.cors.allowed-origin-patterns:*}")
    private String allowedOriginPatterns;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOriginPatterns(splitPatterns(allowedOriginPatterns))
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .maxAge(3600);
    }

    private static String[] splitPatterns(String raw) {
        if (raw == null || raw.isBlank()) {
            return new String[]{"*"};
        }
        String[] parts = raw.split(",");
        for (int i = 0; i < parts.length; i++) {
            parts[i] = parts[i].trim();
        }
        return parts;
    }
}
