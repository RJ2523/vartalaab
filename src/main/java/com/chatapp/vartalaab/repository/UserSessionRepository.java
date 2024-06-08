package com.chatapp.vartalaab.repository;

import org.springframework.data.repository.CrudRepository;

import com.chatapp.vartalaab.redisEntity.UserSessionDetails;

public interface UserSessionRepository extends CrudRepository<UserSessionDetails, String>{
    
}
