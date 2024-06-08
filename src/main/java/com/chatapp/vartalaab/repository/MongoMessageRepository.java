package com.chatapp.vartalaab.repository;


import com.chatapp.vartalaab.document.Message;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface MongoMessageRepository extends MongoRepository<Message, String> {

    @Query("{id:{ $regex: '?0' } }")
    Message findByChatId(String chatId);

    boolean existsById(String chatId);
}
