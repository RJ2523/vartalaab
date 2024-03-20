package com.chatapp.vartalaab.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.chatapp.vartalaab.model.UserModel;
import com.chatapp.vartalaab.service.JwtService;
import com.chatapp.vartalaab.service.UserService;
import com.chatapp.vartalaab.wrapper.UserSignUpWrapper;

@RestController
public class SignUpController {
    
    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserService userService;

    @PostMapping("/auth/signUp")
    public ResponseEntity<?> signUp(@RequestBody UserModel userModel){
        UserSignUpWrapper userSignUpWrapper = userService.validateAndCreateUser(userModel);
        if(!userSignUpWrapper.isUserSignUpSuccessful())
            return new ResponseEntity<>(userSignUpWrapper.getIssue(), HttpStatus.CONFLICT);

        return new ResponseEntity<>(jwtService.generateToken(userModel.getUsername()),
                                                                         HttpStatus.CREATED);
    }
}
