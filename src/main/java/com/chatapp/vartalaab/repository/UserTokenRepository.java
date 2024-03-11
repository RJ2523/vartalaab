package com.chatapp.vartalaab.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.chatapp.vartalaab.redisEntity.UserTokenEntity;


//Redis Repository For UserTokenEntity Class
@Repository
public interface UserTokenRepository extends CrudRepository<UserTokenEntity, String>{
    
}
