package com.chatapp.vartalaab.service;

import com.chatapp.vartalaab.dto.MessageDto;
import com.chatapp.vartalaab.redisEntity.MessageEntity;
import com.chatapp.vartalaab.redisEntity.OfflineMessageIds;
import com.chatapp.vartalaab.repository.MessageRepository;
import com.chatapp.vartalaab.repository.OfflineMessageIdsRepository;
import com.chatapp.vartalaab.utils.GeneralUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class MessageService {

    private MessageRepository messageRepository;

    private OfflineMessageIdsRepository offlineMessageIdsRepository;

    private UserSessionService userSessionService;

    @Autowired
    private ConcurrentHashMap<String, WebSocketSession> sessionMap;

    public MessageService(MessageRepository messageRepository, OfflineMessageIdsRepository offlineMessageIdsRepository, UserSessionService userSessionService){
        this.messageRepository = messageRepository;
        this.offlineMessageIdsRepository = offlineMessageIdsRepository;
        this.userSessionService = userSessionService;
    }

    public List<String> getMessagesFromCache(String username) throws IOException {
        log.debug(">> getMessagesFromCache");
        List<String> messages = new ArrayList<String>();
        Optional<OfflineMessageIds> offlineMessageIds = offlineMessageIdsRepository.findById(username);
        if(offlineMessageIds.isPresent()) {
            List<String> messageIds = offlineMessageIds.get().getMessageIds();
            for (String messageId : messageIds) {
                Optional<MessageEntity> message = messageRepository.findById(messageId);
                if(message.isPresent()) {
                    MessageDto messageDto = new MessageDto(message.get().getSender(), message.get().getMessage(), message.get().getTimestamp());
                    messages.add(messageDto.toString()); //returning JSON message in text format
                    this.sendMessageToRecipient(messageDto.getSender(), new TextMessage(GeneralUtility.ACK_FOR_RECEIVED));
                    messageRepository.deleteById(messageId);
                }
            }
            offlineMessageIdsRepository.deleteById(username);
        }
        return messages;
    }

    public void saveMessagesToCache(MessageDto messageDto){
        log.debug(">> saveMessagesToCache");
        MessageEntity messageEntity = new MessageEntity(messageDto.getSender(), messageDto.getMessage(), messageDto.getTimestamp());
        messageRepository.save(messageEntity);
        String messageId = messageEntity.getId();
        OfflineMessageIds tempOfileMessageIds = null;
        Optional<OfflineMessageIds> offlineMessageIds = offlineMessageIdsRepository.findById(messageDto.getReceiver());
        tempOfileMessageIds = offlineMessageIds.isPresent() ? offlineMessageIds.get() : new OfflineMessageIds(messageDto.getReceiver());
        tempOfileMessageIds.getMessageIds().add(messageId);
        offlineMessageIdsRepository.save(tempOfileMessageIds);
    }

    public void sendMessageToRecipient(String messageReceiver, TextMessage message) throws IOException {
        for(String webSocketSessionId: userSessionService.getUserWebSocketSessionIds(messageReceiver)){
            sessionMap.get(webSocketSessionId).sendMessage(message);
            //Todo: save msg to MongoDB
        }
    }
}
