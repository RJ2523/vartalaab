package com.chatapp.vartalaab.repository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.chatapp.vartalaab.entity.User;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

    boolean existsByUsername(String username);
}
