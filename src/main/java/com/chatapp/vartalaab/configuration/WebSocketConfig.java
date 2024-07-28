package com.chatapp.vartalaab.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import java.util.concurrent.ConcurrentHashMap;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(getCustomWebSocketHandler(), "/sendMessage")
                .addInterceptors(new CustomHandshakeInterceptor()).setAllowedOrigins("http://localhost:1234");
    }

    @Bean
    public ConcurrentHashMap<String, WebSocketSession> sessionMap(){
        return new ConcurrentHashMap<String, WebSocketSession>();
    }

    @Bean
    public ConcurrentHashMap<String, String> sessionUsernameMap(){
        return new ConcurrentHashMap<String, String>();
    }

    @Bean
    public CustomWebSocketHandler getCustomWebSocketHandler(){
        return new CustomWebSocketHandler();
    }
}