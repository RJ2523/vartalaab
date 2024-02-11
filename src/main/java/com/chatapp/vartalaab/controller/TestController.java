package com.chatapp.vartalaab.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chatapp.vartalaab.service.JwtService;

@RestController
public class TestController {

    @Autowired
    private JwtService jwtService;

    @PostMapping("/post")
    public ResponseEntity<String> testPost(){
        System.out.println("hello");
        //String token = jwtService.generateToken("admin");
        return new ResponseEntity<>("token",HttpStatus.OK);
    }
    @GetMapping("/get")
    public ResponseEntity<String> testGet(){
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
