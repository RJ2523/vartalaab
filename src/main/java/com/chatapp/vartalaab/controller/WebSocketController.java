package com.chatapp.vartalaab.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.chatapp.vartalaab.dto.MessageDto;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;

@Controller
public class WebSocketController {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/chat")
    public void handleMessage(String message, SimpMessageHeaderAccessor simpMessageHeaderAccessor) throws Exception {
        // Process the message and the authToken
        //System.out.println("AuthToken: " + authToken);
        MessageDto messageDto = new MessageDto("rs", "one plus 8t", new Date());
        //simpMessagingTemplate.convertAndSendToUser(message, message, message);
        System.out.println("header : " + simpMessageHeaderAccessor.getFirstNativeHeader("token"));
        System.out.println("message: " +  message);
        simpMessagingTemplate.convertAndSend("/topic/message",  messageDto);
        
    }

    // @SubscribeMapping("/messages")
    // public String onSubscribe(StompHeaderAccessor headerAccessor) {
    //     //String authToken = headerAccessor.getFirstNativeHeader("Authorization");
    //     System.out.println("Subscription AuthToken: ");
    //     return "Subscribed successfully!";
    // }

    // @EventListener
    // public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
    //     StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
    //     String sessionId = headerAccessor.getSessionId();
    //     System.out.println("Disconnected session: " + sessionId);
    //     System.out.println("Disconnect reason: " + headerAccessor.getMessage());
    // }

    // @EventListener
    // public void handleWebSocketConnectListener(SessionConnectedEvent event) {
    //     StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
    //     String sessionId = headerAccessor.getSessionId();
    //     System.out.println("Connected session: " + sessionId);
    // }
}