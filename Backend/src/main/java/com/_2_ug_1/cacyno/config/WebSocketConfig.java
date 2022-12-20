package com._2_ug_1.cacyno.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * configuration for websockets
 */
@Configuration
public class WebSocketConfig {
    /**
     * object to add websocket endpoints
     *
     * @return a serverEndpointExporter bean
     */
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
}
