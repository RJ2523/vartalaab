package com.chatapp.vartalaab.repository;
import com.chatapp.vartalaab.entity.User;


public interface UserRepository{
    User findUserByUsername(String username);
}
