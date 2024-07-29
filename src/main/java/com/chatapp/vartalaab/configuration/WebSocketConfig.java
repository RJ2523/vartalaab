package com.chatapp.vartalaab.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import java.util.concurrent.ConcurrentHashMap;

@Configuration
@EnableWebSocket
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {


    @Autowired 
    private CustomChannelInterceptor customChannelInterceptor;
    // @Override
    // public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
    //     registry.addHandler(getCustomWebSocketHandler(), "/sendMessage")
    //             .addInterceptors(new HttpSessionHandshakeInterceptor()).setAllowedOrigins("/*");
    // }

    @Bean
    public ConcurrentHashMap<String, WebSocketSession> sessionMap(){
        return new ConcurrentHashMap<String, WebSocketSession>();
    }

    // @Bean
    // public CustomWebSocketHandler getCustomWebSocketHandler(){
    //     return new CustomWebSocketHandler();
    // }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws").setAllowedOriginPatterns("*").withSockJS();
    }

     @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(customChannelInterceptor);
    }
    
}