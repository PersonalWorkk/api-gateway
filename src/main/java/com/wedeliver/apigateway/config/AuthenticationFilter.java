package com.wedeliver.apigateway.config;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.Claim;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;

import io.netty.handler.codec.http.HttpMethod;
import reactor.core.publisher.Mono;

@RefreshScope
@Component
public class AuthenticationFilter implements GatewayFilter{
    
    // The endpoints in here can be accessed without any permission
    private final List<Request> OPEN_ENDPOINTS = new LinkedList<>(){{
        add(new Request(HttpMethod.POST, Pattern.compile("\\/api\\/auth\\/login")));
        add(new Request(HttpMethod.POST, Pattern.compile("\\/api\\/auth\\/register")));
    }};

    /**
     * Maps the urls to the list of roles that are allowed to access it
     */
    private final Map<Request, ImmutableList<String>> RESTRICTED_ENDPOINTS = new HashMap<>(){{
        put(new Request(HttpMethod.GET,  Pattern.compile("\\/api\\/auth\\/users\\/role")), ImmutableList.of("ADMIN"));
        put(new Request(HttpMethod.DELETE,  Pattern.compile("\\/api\\/auth\\/users\\/[a-zA-Z0-9]+")), ImmutableList.of("ADMIN"));
        put(new Request(HttpMethod.PUT,  Pattern.compile("\\/api\\/auth\\/users")), ImmutableList.of("ADMIN", "CUSTOMER"));
    }};

    public AuthenticationFilter(){}

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain){
        ServerHttpRequest request = exchange.getRequest();
        var path = request.getURI().getPath();

        if(isOpenEndpoint(request, path)){
            return chain.filter(exchange);
        }

        for (Request reqDefinition : RESTRICTED_ENDPOINTS.keySet()) {
            if(reqDefinition.getPattern().matcher(path).matches() && reqDefinition.getHttpMethod().equals(request.getMethod())) {
                if (isAuthMissing(request)) {
                    Mono<Void> response = respondWithUnauthorized(exchange, "Token is empty");
                    if (response != null) return response;
                }

                var authHeader = getAuthHeader(request);
                var tokenString = authHeader.split(" ")[1];
                var token = JWT.decode(tokenString);

                var allowedRoles = RESTRICTED_ENDPOINTS.get(reqDefinition);
                Map<String, Claim> claims = token.getClaims();

                if(claims == null){
                    Mono<Void> response = respondWithUnauthorized(exchange, "Unauthorized");
                    if (response != null) return response;
                }

                var authorities = claims.get("authorities");
                var roleInToken = authorities.asString().split("_")[1];

                if (!allowedRoles.contains(roleInToken)) {
                    Mono<Void> response = respondWithUnauthorized(exchange, "Unauthorized");
                    if (response  != null) return response;
                }
            }
        }

        return chain.filter(exchange);
    }

    private Mono<Void> respondWithUnauthorized(ServerWebExchange exchange, String cause){
        var response = exchange.getResponse();
        Map<String, Object> responseData = Maps.newHashMap();
        responseData.put("code", 401);
        responseData.put("message", "Illegal request");
        responseData.put("cause", cause);

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            byte[] data = objectMapper.writeValueAsBytes(responseData);

            DataBuffer buffer = response.bufferFactory().wrap(data);
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
            return response.writeWith(Mono.just(buffer));
        } catch (JsonProcessingException e) {
            System.out.println("{}" + e);
        }
        return null;
    }

    private boolean isOpenEndpoint(ServerHttpRequest request, String path) {
        return OPEN_ENDPOINTS.stream().anyMatch(req -> req.getPattern().matcher(path).matches() && Objects.equals(request.getMethod(), req.getHttpMethod()));
    }

    private boolean isAuthMissing(ServerHttpRequest request) {
        return !request.getHeaders().containsKey("Authorization");
    }

    private String getAuthHeader(ServerHttpRequest request) {
        return request.getHeaders().getOrEmpty("Authorization").get(0);
    }
}
