package com.chatapp.vartalaab.controller;

import com.chatapp.vartalaab.document.Message;
import com.chatapp.vartalaab.repository.MongoMessageRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin
@RestController
@Slf4j
public class MessageController {

    private MongoMessageRepository mongoMessageRepository;

    MessageController(MongoMessageRepository mongoMessageRepository){
        this.mongoMessageRepository=mongoMessageRepository;
    }

    //fetches messages based on both chatID and UserID
    @PreAuthorize("#chatId.contains(authentication.principal.username)")
    @GetMapping("/getMessages/{chatId}")
    public ResponseEntity<?> getMessages(@PathVariable String chatId){
        List<Message> message = mongoMessageRepository.findByChatId(chatId);
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

}
