package com.chatapp.vartalaab.service;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.chatapp.vartalaab.entity.User;
import com.chatapp.vartalaab.model.UserModel;
import com.chatapp.vartalaab.repository.UserRepository;
import com.chatapp.vartalaab.wrapper.UserSignUpWrapper;

import io.micrometer.common.util.StringUtils;


@Service
public class UserService implements UserDetailsService{

    private UserRepository userRepository;

    private ModelMapper modelMapper;

    private Logger logger = LoggerFactory.getLogger(UserService.class);



    private static String USERNAME_IS_EMPTY = "please provide a valid username";

    private static String USERNAME_ALREAD_EXISTS = "Username already exists, please choose different username";

    public UserService(UserRepository userRepository, ModelMapper modelMapper){
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.debug(">> loadUserByUsername");
        Optional<User> user = userRepository.findById(username);
        if(!user.isPresent()){
            logger.debug(">> user does not exist");
            throw new UsernameNotFoundException(username);
        }
        logger.debug("<< loadUserByUsername");
        user.get().setRole("USER");
        return user.get();
    }

    public UserSignUpWrapper validateAndCreateUser(UserModel userModel){
        logger.debug(">> validateAndCreateUser");
        UserSignUpWrapper userSignUpObj = new UserSignUpWrapper();
        userSignUpObj.setUserSignUpSuccessful(false);
        // does username already taken
        if(StringUtils.isEmpty(userModel.getUsername())){
            userSignUpObj.setIssue(USERNAME_IS_EMPTY);
            return userSignUpObj;
        } 
        if(userRepository.existsByUsername(userModel.getUsername())){
            userSignUpObj.setIssue(USERNAME_ALREAD_EXISTS);
            return userSignUpObj;
        }
        User newUserDetails = modelMapper.map(userModel, User.class);
        newUserDetails.setPassword(new BCryptPasswordEncoder().encode(userModel.getPassword()));
        userRepository.save(newUserDetails);
        userSignUpObj.setUserSignUpSuccessful(true);
        logger.debug("<< validateAndCreateUser");
        return userSignUpObj;
    }

}
