package com.chatapp.vartalaab.configuration;

import java.util.NoSuchElementException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import com.chatapp.vartalaab.redisEntity.UserTokenEntity;
import com.chatapp.vartalaab.repository.UserTokenRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomLogoutHandler implements LogoutHandler{

    private UserTokenRepository userTokenRepository;

    public CustomLogoutHandler(UserTokenRepository userTokenRepository){
        this.userTokenRepository = userTokenRepository;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
    
        String authHeader = request.getHeader("Authorization");
        String token = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring("Bearer ".length());
        }

        UserTokenEntity userTokenEntity = userTokenRepository.findById(token)
                                            .orElseThrow(()-> new NoSuchElementException());
        userTokenEntity.setLoggedOut(true); 
        userTokenRepository.save(userTokenEntity);
    }
    
}
