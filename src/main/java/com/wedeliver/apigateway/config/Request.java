package com.wedeliver.apigateway.config;

import java.util.regex.Pattern;

import io.netty.handler.codec.http.HttpMethod;

public class Request {
    private HttpMethod httpMethod;
    private Pattern pattern;

    public Request(HttpMethod httpMethod, Pattern pattern){
        this.httpMethod = httpMethod;
        this.pattern = pattern;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }
    public Pattern getPattern() {
        return pattern;
    }
}
