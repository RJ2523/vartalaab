package com.chatapp.vartalaab.controllerAdvice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import com.chatapp.vartalaab.exception.MaximumSessionReachedException;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class GeneralControllerAdvicer {
    
    @ExceptionHandler(MaximumSessionReachedException.class)
    public final ResponseEntity<?> handleMaximumSessionReached(Exception exception, WebRequest webRequest){
        log.error("User has reached maximum login limit", exception.getMessage());
        return new ResponseEntity<>("Maximum Device limit Reached", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
