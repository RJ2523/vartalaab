package com.chatapp.vartalaab.controller;

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
import com.chatapp.vartalaab.redisEntity.UserTokenEntity;
import com.chatapp.vartalaab.service.JwtService;
import com.chatapp.vartalaab.service.UserTokenService;


@RestController
public class LoginController{

    private AuthenticationManager authenticationManager;

    private JwtService jwtService;
    
    private UserTokenService userTokenService;

    public LoginController(AuthenticationManager authenticationManager, 
                            JwtService jwtService, 
                            UserTokenService userTokenService){
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userTokenService = userTokenService;
    }
    @PostMapping("/auth/login")
    public ResponseEntity<?> doLogin(@RequestBody AuthRequest authRequest){
        Authentication authentication = authenticationManager.
                                        authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(),
                                        authRequest.getPassword()));
        if(authentication.isAuthenticated()){
            String jwtToken = jwtService.generateToken(authRequest.getUsername());
            //saving token publish details to redis cache for logout/blacklist purpose
            userTokenService.saveUserTokenDetailsToCache(new UserTokenEntity(jwtToken, authRequest.getUsername(), false));
            return ResponseEntity.ok()
            .header(HttpHeaders.AUTHORIZATION, jwtToken)
            .build();
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
}
