package com.chatapp.vartalaab.repository;


import com.chatapp.vartalaab.document.Message;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface MongoMessageRepository extends MongoRepository<Message, String> {

    @Query("{id:{ $regex: '?0' } }")
    List<Message> findByChatId(String chatId);

    boolean existsById(String chatId);
}
