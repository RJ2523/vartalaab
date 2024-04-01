package com.chatapp.vartalaab.redisEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import lombok.Data;

@RedisHash("user_session")
@Data
public class UserSessionDetails {

    @Id
    private String userSessionKey;
    private int activeSessions;
    //mapping of token - isLoggedOut
    private Map<String, Boolean> tokenValidityDetails;
    private List<String> webSocketSessionIds;

    public UserSessionDetails(String userSessionKey, int activeSessions, Map<String, Boolean> tokenValidityDetails) {
        this.userSessionKey = userSessionKey;
        this.activeSessions = activeSessions;
        this.tokenValidityDetails = tokenValidityDetails;
        webSocketSessionIds = new ArrayList<String>();
    }



    public boolean isLoggedOut(String token){
        return tokenValidityDetails.getOrDefault(token, true);
    }
}
