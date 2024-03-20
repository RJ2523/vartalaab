package com.chatapp.vartalaab.configuration;

import java.util.NoSuchElementException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import com.chatapp.vartalaab.exception.TokenNotFoundException;
import com.chatapp.vartalaab.redisEntity.UserSessionDetails;
import com.chatapp.vartalaab.service.JwtService;
import com.chatapp.vartalaab.service.UserSessionService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CustomLogoutHandler implements LogoutHandler{

    private UserSessionService userSessionService;

    private JwtService jwtService;

    public CustomLogoutHandler(UserSessionService userSessionService, JwtService jwtService){
        this.userSessionService = userSessionService;
        this.jwtService = jwtService;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
    
        String authHeader = request.getHeader("Authorization");
        String token = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring("Bearer ".length());
        }
        if(token==null)
            throw new TokenNotFoundException("Token not Found");
        
        String user = jwtService.extractUsername(token);
        UserSessionDetails userSessionDetails = userSessionService.getUserSessionDetails(user)
                                                .orElseThrow(()-> new NoSuchElementException());
        //blacklisting the token
        userSessionDetails.getTokenValidityDetails().put(token, true);
        userSessionDetails.setActiveSessions(userSessionDetails.getActiveSessions()-1);
        userSessionService.saveUserSessionDetails(userSessionDetails);
        log.info("logged out for user : {}" , user);
    }
    
}
