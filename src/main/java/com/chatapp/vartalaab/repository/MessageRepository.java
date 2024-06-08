package com.chatapp.vartalaab.repository;

import com.chatapp.vartalaab.redisEntity.MessageEntity;
import org.springframework.data.repository.CrudRepository;

public interface MessageRepository extends CrudRepository<MessageEntity, String> {

}
