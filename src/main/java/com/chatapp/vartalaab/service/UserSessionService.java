package com.chatapp.vartalaab.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.chatapp.vartalaab.redisEntity.UserSessionDetails;
import com.chatapp.vartalaab.repository.UserSessionRepository;
import com.chatapp.vartalaab.utils.GeneralUtility;

@Service
public class UserSessionService {

    private UserSessionRepository userSessionRepository;

    public UserSessionService(UserSessionRepository userSessionRepository){
        this.userSessionRepository = userSessionRepository;
    }

    public void saveUserSessionDetails(UserSessionDetails userSessionDetails){
        userSessionRepository.save(userSessionDetails);
    }

    public Optional<UserSessionDetails> getUserSessionDetails(String username){
        return userSessionRepository.findById(username  + GeneralUtility.SESSION_APPENDER);
    }

    public void updateUserWebSocketSessionDetails(String username, String sessionId, boolean isOnline){
        Optional<UserSessionDetails> userSession = this.getUserSessionDetails(username);
        if(isOnline){
            userSession.get().getWebSocketSessionIds().add(sessionId);
        }
        else{
            userSession.get().getWebSocketSessionIds().remove(sessionId);
        }
        userSessionRepository.save(userSession.get());
    }

    public boolean IsUserOnline(String username){
        Optional<UserSessionDetails> userSessOptional =  this.getUserSessionDetails(username);
        return userSessOptional.isPresent() && userSessOptional.get().getActiveSessions() > 0;
    }

    public List<String> getUserWebSocketSessionIds(String username){
        Optional<UserSessionDetails> userSessOptional =  this.getUserSessionDetails(username);
        if(userSessOptional.isPresent()) {
            return userSessOptional.get().getWebSocketSessionIds();
        }
        return Collections.emptyList();
    }
}
