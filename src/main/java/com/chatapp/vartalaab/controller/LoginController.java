package com.chatapp.vartalaab.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.chatapp.vartalaab.model.AuthRequest;
import com.chatapp.vartalaab.model.AuthResponse;
import com.chatapp.vartalaab.service.JwtService;


@RestController
public class LoginController{

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;
    @PostMapping("/auth/login")
    public ResponseEntity<?> doLogin(@RequestBody AuthRequest authRequest){
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(),authRequest.getPassword()));
        if(authentication.isAuthenticated()){
            return ResponseEntity.ok()
            .header(HttpHeaders.AUTHORIZATION, jwtService.generateToken(authRequest.getUsername()))
            .build();
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
}
