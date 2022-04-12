package com.wedeliver.apigateway.config;

import com.wedeliver.apigateway.filter.JwtAuthenticationFilter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {
    
    // Filter used to routes requests to the appropriate microservice
    @Autowired
    private JwtAuthenticationFilter filter;

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder){
        return builder.routes()
            // Authentication route
            // Calls the service-account endpoints
            .route("auth", r -> r
                .path("/auth/**")
                .filters(f -> f.filter(filter))
                .uri("http://localhost:8081/")
            )
            .build();
    }
}
