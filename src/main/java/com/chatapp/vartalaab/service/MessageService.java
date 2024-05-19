package com.chatapp.vartalaab.service;

import com.chatapp.vartalaab.converter.ObjectConverter;
import com.chatapp.vartalaab.document.Message;
import com.chatapp.vartalaab.dto.MessageDto;
import com.chatapp.vartalaab.redisEntity.MessageEntity;
import com.chatapp.vartalaab.redisEntity.OfflineMessageIds;
import com.chatapp.vartalaab.repository.MessageRepository;
import com.chatapp.vartalaab.repository.MongoMessageRepository;
import com.chatapp.vartalaab.repository.OfflineMessageIdsRepository;
import com.chatapp.vartalaab.utils.GeneralUtility;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
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

    private MongoMessageRepository mongoMessageRepository;

    private OfflineMessageIdsRepository offlineMessageIdsRepository;

    private UserSessionService userSessionService;

    @Autowired
    private ConcurrentHashMap<String, WebSocketSession> sessionMap;

    private MongoTemplate mongoTemplate;

    public MessageService(MessageRepository messageRepository, OfflineMessageIdsRepository offlineMessageIdsRepository, UserSessionService userSessionService, MongoMessageRepository mongoMessageRepository, MongoTemplate mongoTemplate){
        this.messageRepository = messageRepository;
        this.offlineMessageIdsRepository = offlineMessageIdsRepository;
        this.userSessionService = userSessionService;
        this.mongoMessageRepository = mongoMessageRepository;
        this.mongoTemplate = mongoTemplate;
    }

    public List<String> getMessagesFromCache(String username) throws IOException {
        log.debug(">> getMessagesFromCache");
        List<String> messages = new ArrayList<String>();
        Optional<OfflineMessageIds> offlineMessageIds = offlineMessageIdsRepository.findById(username);
        if(offlineMessageIds.isPresent()) {
            List<String> messageIds = offlineMessageIds.get().getMessageIds();
            for (String messageId : messageIds) {
                MessageEntity message = messageRepository.findById(messageId).orElseThrow(() -> new NoSuchElementException());
                MessageDto messageDto = new MessageDto(message.getSender(), message.getMessage(), message.getTimestamp());
                messages.add(messageDto.toString()); //returning JSON message in text format
                this.forwardTheMessageToReceiver(messageDto.getSender(), new TextMessage(GeneralUtility.AckForReceived), false);
                messageRepository.deleteById(messageId);
            }
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

    public void forwardTheMessageToReceiver(String messageReceiver, TextMessage message, boolean isAck) throws IOException {
        for(String webSocketSessionId: userSessionService.getUserWebSocketSessionIds(messageReceiver)){
            sessionMap.get(webSocketSessionId).sendMessage(message);
        }
        if(!isAck) {
            saveMessagesToDB(message);
        }
    }

    public void saveMessagesToDB(TextMessage textMessage) throws JsonProcessingException {
        Message message = new Message();
        ObjectMapper objectMapper = new ObjectMapper();
        MessageDto messageDto = objectMapper.readValue(textMessage.getPayload(), MessageDto.class);
        if(messageDto.getSender().compareTo(messageDto.getReceiver())>0)
            message.setId(messageDto.getReceiver() + "_" + messageDto.getSender());
        else
            message.setId(messageDto.getSender() + "_" + messageDto.getReceiver());
        boolean isChatPresent = mongoMessageRepository.existsById(message.getId());
        if(!isChatPresent){
            message.setMessages(List.of(messageDto));
            mongoMessageRepository.save(message);
        }
        else{
            Query query = Query.query(Criteria.where("_id").is(message.getId()));
            Update update = new Update().addToSet("messages", new ObjectConverter().convert(messageDto));
            mongoTemplate.updateFirst(query, update,"Messages");
        }
    }
}
