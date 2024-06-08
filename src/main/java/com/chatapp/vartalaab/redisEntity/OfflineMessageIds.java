package com.chatapp.vartalaab.redisEntity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.ArrayList;
import java.util.List;

@Data
@RedisHash
public class OfflineMessageIds {
    @Id
    private String receiver;

    private List<String> MessageIds;

    public OfflineMessageIds(String receiver) {
        this.receiver = receiver;
        this.MessageIds = new ArrayList<String>();
    }
}
