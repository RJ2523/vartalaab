package com.chatapp.vartalaab.controller;

import java.util.HashMap;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.aggregation.VariableOperators.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.chatapp.vartalaab.redisEntity.UserSessionDetails;
import com.chatapp.vartalaab.repository.UserSessionRepository;
import com.chatapp.vartalaab.service.JwtService;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private JwtService jwtService;

    @Autowired
    UserSessionRepository userSessionRepository;

    @PostMapping("/post")
    public ResponseEntity<String> testPost(@RequestParam String username){
        java.util.Map<String,Boolean> map = new HashMap<>();
        map.put("abc", false);
        UserSessionDetails userSessionDetails = new UserSessionDetails(username+"_session", 1, map);
        userSessionRepository.save(userSessionDetails);
        Optional<UserSessionDetails> us = userSessionRepository.findById(username+"_session");
        System.out.println("isLoggeOut :  " +us.get().getTokenValidityDetails().get("abc"));    
        return new ResponseEntity<>("token",HttpStatus.OK);
    }
    @GetMapping("/get")
    public ResponseEntity<String> testGet(){
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
