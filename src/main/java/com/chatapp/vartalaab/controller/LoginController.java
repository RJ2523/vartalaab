package com.chatapp.vartalaab.controller;

import java.util.HashMap;
import java.util.Optional;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.chatapp.vartalaab.exception.MaximumSessionReachedException;
import com.chatapp.vartalaab.model.AuthRequest;
import com.chatapp.vartalaab.redisEntity.UserSessionDetails;
import com.chatapp.vartalaab.service.JwtService;
import com.chatapp.vartalaab.service.UserSessionService;
import com.chatapp.vartalaab.utils.GeneralUtility;

import lombok.extern.slf4j.Slf4j;


@RestController
@Slf4j
public class LoginController{

    private AuthenticationManager authenticationManager;

    private JwtService jwtService;
    
    private UserSessionService userSessionService;

    public LoginController(AuthenticationManager authenticationManager, 
                            JwtService jwtService, 
                            UserSessionService userSessionService){
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userSessionService = userSessionService;
    }
    @PostMapping("/auth/login")
    public ResponseEntity<?> doLogin(@RequestBody AuthRequest authRequest){
        log.info("login request for username: {}", authRequest.getUsername());
        Authentication authentication = authenticationManager.
                                        authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(),
                                        authRequest.getPassword()));
        if(!ObjectUtils.isEmpty(authentication) && authentication.isAuthenticated()){
            log.info("authentication successful for user: {}" , authRequest.getUsername());
            String jwtToken = jwtService.generateToken(authRequest.getUsername());
            Optional<UserSessionDetails> userSessOptional =  userSessionService.getUserSessionDetails(authRequest.getUsername());
            UserSessionDetails userSessionDetails = null;
            //is logged in one or more device
            if(userSessOptional.isPresent()){
                userSessionDetails = userSessOptional.get();

                //checking if the login devices is already at threshold
                if(userSessionDetails.getActiveSessions()>=3)
                    throw new MaximumSessionReachedException("Maxium Device Limit Reached");

                userSessionDetails.setActiveSessions(userSessionDetails.getActiveSessions()+1);
                userSessionDetails.getTokenValidityDetails().put(jwtToken, false);
                userSessionService.saveUserSessionDetails(userSessionDetails);
            }else{  
                userSessionDetails = new UserSessionDetails(authRequest.getUsername()+GeneralUtility.SESSION_APPENDER, 1 , new HashMap<>(){{put(jwtToken, false);}});
                userSessionService.saveUserSessionDetails(userSessionDetails);
            }
            log.info("user : {} is logged in {} devices", authRequest.getUsername(), userSessionDetails.getActiveSessions());
            return ResponseEntity.ok()
            .header(HttpHeaders.AUTHORIZATION, jwtToken)
            .build();
        }
        log.info("login details not found for user: {}", authRequest.getUsername());
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
}
