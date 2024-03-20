package com.chatapp.vartalaab.redisEntity;

import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import com.chatapp.vartalaab.utils.GeneralUtility;

import lombok.AllArgsConstructor;
import lombok.Data;

@RedisHash("user_session")
@Data
@AllArgsConstructor
public class UserSessionDetails {
    
    @Id
    private String userSessionKey;
    private int activeSessions;
    //mapping of token - isLoggedOut
    private Map<String, Boolean> tokenValidityDetails;

    public boolean isLoggedOut(String token){   
        return tokenValidityDetails.getOrDefault(token, true);
    }
}
