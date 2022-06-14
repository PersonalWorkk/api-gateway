package com.wedeliver.apigateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

import com.wedeliver.apigateway.config.AuthenticationFilter;

@SpringBootApplication
public class ApiGatewayApplication {

	@Autowired
    AuthenticationFilter filter;

	public static void main(String[] args) {
		SpringApplication.run(ApiGatewayApplication.class, args);
	}

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder){
        return builder.routes()
			// Authentication route
            // Calls the service-account endpoints
			.route(routeSpec ->
				routeSpec.path("/api/auth/**")
					.filters(f -> f.filter(filter))
					.uri("http://localhost:8081/")
			)
			.build();
    }

}
