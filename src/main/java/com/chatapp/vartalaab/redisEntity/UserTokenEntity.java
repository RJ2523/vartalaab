package com.chatapp.vartalaab.redisEntity;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import lombok.Data;

@RedisHash("token")
@Data
public class UserTokenEntity {
  
    @Id
    private String jwtToken;
    private String username;
    private boolean isLoggedOut;

    public UserTokenEntity(String jwtToken, String username, boolean isLoggedOut) {
        this.jwtToken = jwtToken;
        this.username = username;
        this.isLoggedOut = isLoggedOut;
    }
}
