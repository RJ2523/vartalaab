package com.chatapp.vartalaab.redisEntity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.Date;

@Data
@RedisHash
public class MessageEntity {

    @Id
    private String Id; //Spring Data Redis will  auto generate IDs
    private String sender;
    private String message;
    private Date timestamp;

    public MessageEntity(String sender, String message, Date timestamp) {
        this.sender = sender;
        this.message = message;
        this.timestamp = timestamp;
    }
}
