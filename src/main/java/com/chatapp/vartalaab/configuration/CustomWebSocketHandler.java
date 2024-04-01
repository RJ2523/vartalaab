package com.chatapp.vartalaab.configuration;

import com.chatapp.vartalaab.dto.MessageDto;
import com.chatapp.vartalaab.service.MessageService;
import com.chatapp.vartalaab.service.UserSessionService;
import com.chatapp.vartalaab.utils.GeneralUtility;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    private UserSessionService userSessionService;

    @Autowired
    private MessageService messageService;


    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        MessageDto messageDto = objectMapper.readValue(message.getPayload(), MessageDto.class);
        String username = session.getPrincipal().getName();
        messageDto.setSender(username);
        //ACK for message sent
        session.sendMessage(new TextMessage(GeneralUtility.AckForSent));
        //check if receiver is online
        String messageReceiver = messageDto.getReceiver();
        if(userSessionService.checkIfUserIsOnline(messageReceiver)){
            messageService.forwardTheMessageToReceiver(messageReceiver, message);
            //ACK for message received
            session.sendMessage(new TextMessage(GeneralUtility.AckForReceived));
        }
        else{
            messageService.saveMessagesToCache(messageDto);
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws IOException {
        log.info("WebSocket connection established");
        sessionMap.put(session.getId(), session);
        String username = session.getPrincipal().getName();
        userSessionService.updateUserWebSocketSessionDetails(username, session.getId(), true);

        //process the messages stored in redis during client's offline period
        List<String> messages = messageService.getMessagesFromCache(username);
        for(String message: messages){
            session.sendMessage(new TextMessage(message));
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus){
        log.info("WebSocket connection closed");
        sessionMap.remove(session.getId());
        String username = session.getPrincipal().getName();
        userSessionService.updateUserWebSocketSessionDetails(username, session.getId(), false);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error(exception.toString());
    }

}
