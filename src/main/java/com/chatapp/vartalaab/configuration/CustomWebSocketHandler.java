package com.chatapp.vartalaab.configuration;

import com.chatapp.vartalaab.dto.MessageDto;
import com.chatapp.vartalaab.service.JwtService;
import com.chatapp.vartalaab.service.MessageService;
import com.chatapp.vartalaab.service.UserService;
import com.chatapp.vartalaab.service.UserSessionService;
import com.chatapp.vartalaab.utils.GeneralUtility;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class CustomWebSocketHandler extends TextWebSocketHandler {

    @Autowired
    private ConcurrentHashMap<String, WebSocketSession> sessionMap;

    @Autowired
    private ConcurrentHashMap<String, String> sessionUsernameMapping;

    @Autowired
    private UserSessionService userSessionService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserService userService;


    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        System.out.println(Thread.currentThread().getId());
        ObjectMapper objectMapper = new ObjectMapper();
        MessageDto messageDto = objectMapper.readValue(message.getPayload(), MessageDto.class);
        String jwt = messageDto.getJwt();
        if(jwt!=null){
            String username = jwtService.extractUsername(jwt.substring("Bearer ".length()));
            UserDetails userDetails = userService.loadUserByUsername(username);
            if(jwtService.validateToken(jwt.substring("Bearer ".length()),userDetails)) {
                session.getAttributes().put("authenticated", true);
                sessionMap.put(session.getId(), session);
                sessionUsernameMapping.put(session.getId(), username);
                userSessionService.updateUserWebSocketSessionDetails(username, session.getId(), true);
                session.sendMessage(new TextMessage(GeneralUtility.ACK_AUTHENTICATION));

                //process the messages stored in redis during client's offline period
                List<String> messages = messageService.getMessagesFromCache(username);
                for (String msg : messages) {
                    TextMessage textMessage = new TextMessage(msg);
                    session.sendMessage(textMessage);
                    messageService.saveMessagesToDB(textMessage);
                }
            }
            else{
                //invalid jwt
                log.error("Invalid jwt");
                session.close();
            }
        }
        else {
            if((boolean) session.getAttributes().get("authenticated")) {
                //ACK for message sent
                //todo: enhance ACK messages and add concurrent msg support
                session.sendMessage(new TextMessage(GeneralUtility.ACK_FOR_SENT));
                //check if receiver is online
                String messageReceiver = messageDto.getReceiver();
                if (userSessionService.IsUserOnline(messageReceiver)) {
                    messageService.sendMessageToRecipient(messageReceiver, message, false);
                    //ACK for message received
                    session.sendMessage(new TextMessage(GeneralUtility.ACK_FOR_RECEIVED));
                } else {
                    messageService.saveMessagesToCache(messageDto);
                }
            }
            else{
                //user not authenticated
                log.error("User is not authenticated");
                session.close();
            }
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws IOException {
        log.info("WebSocket connection established");
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus){
        log.info("WebSocket connection closed");
        sessionMap.remove(session.getId());
        String username = sessionUsernameMapping.get(session.getId());
        sessionUsernameMapping.remove(session.getId());
        userSessionService.updateUserWebSocketSessionDetails(username, session.getId(), false);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error(exception.toString());
    }

}