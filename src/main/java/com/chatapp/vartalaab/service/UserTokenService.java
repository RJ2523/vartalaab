package com.chatapp.vartalaab.service;

import java.util.Optional;
import org.springframework.stereotype.Service;
import com.chatapp.vartalaab.redisEntity.UserTokenEntity;
import com.chatapp.vartalaab.repository.UserTokenRepository;


@Service
public class UserTokenService {
    
    private UserTokenRepository userTokenRepository;

    public UserTokenService(UserTokenRepository userTokenRepository){
        this.userTokenRepository = userTokenRepository;
    }

    public void saveUserTokenDetailsToCache(UserTokenEntity userTokenEntity){
        userTokenRepository.save(userTokenEntity);
    }

    public Optional<UserTokenEntity> getUserTokenDetails(String jwtToken){
        return userTokenRepository.findById(jwtToken);
    }
}
