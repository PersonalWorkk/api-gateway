package com.wedeliver.apigateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.stereotype.Component;

@Component
public class NettyConfiguration implements WebServerFactoryCustomizer<NettyReactiveWebServerFactory>{
    
    @Value("${server.max-initial-line-length:65536}")
    private int maxInitialLineLength;

    public void customize(NettyReactiveWebServerFactory container){
        container.addServerCustomizers(
            httpServer -> httpServer.httpRequestDecoder(
                httpRequestDecoderSpec -> httpRequestDecoderSpec.maxInitialLineLength(maxInitialLineLength)
            )
        );
    }
}
