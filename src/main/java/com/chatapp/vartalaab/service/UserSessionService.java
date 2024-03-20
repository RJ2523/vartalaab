package com.chatapp.vartalaab.service;

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
}
