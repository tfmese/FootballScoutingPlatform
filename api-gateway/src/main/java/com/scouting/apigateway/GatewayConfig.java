package com.scouting.apigateway;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RequestPredicates;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

@Configuration
public class GatewayConfig {

    @Value("${PLAYER_SERVICE_URL:http://localhost:8081}")
    private String playerServiceUrl;

    @Value("${SCOUTING_SERVICE_URL:http://localhost:8082}")
    private String scoutingServiceUrl;

    @Bean
    public RouterFunction<ServerResponse> playerRoutes() {
        return GatewayRouterFunctions.route("player-route")
                .route(RequestPredicates.path("/api/players/**").or(RequestPredicates.path("/api/players")),
                        HandlerFunctions.http())
                .filter((request, next) -> {
                    org.springframework.web.servlet.function.ServerRequest forwarded =
                            org.springframework.web.servlet.function.ServerRequest.from(request)
                                    .attribute(org.springframework.cloud.gateway.server.mvc.common.MvcUtils.GATEWAY_REQUEST_URL_ATTR,
                                            java.net.URI.create(playerServiceUrl + request.requestPath()))
                                    .build();
                    return next.handle(forwarded);
                })
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> scoutingRoutes() {
        return GatewayRouterFunctions.route("scouting-route")
                .route(RequestPredicates.path("/api/scouts/**").or(RequestPredicates.path("/api/scouts")),
                        HandlerFunctions.http())
                .filter((request, next) -> {
                    org.springframework.web.servlet.function.ServerRequest forwarded =
                            org.springframework.web.servlet.function.ServerRequest.from(request)
                                    .attribute(org.springframework.cloud.gateway.server.mvc.common.MvcUtils.GATEWAY_REQUEST_URL_ATTR,
                                            java.net.URI.create(scoutingServiceUrl + request.requestPath()))
                                    .build();
                    return next.handle(forwarded);
                })
                .build();
    }
}
