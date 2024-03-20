package com.chatapp.vartalaab.exception;

import lombok.Data;

@Data
public class MaximumSessionReachedException extends RuntimeException{
    private String errorMessage;
    
    public MaximumSessionReachedException(String errorMessage){
        this.errorMessage = errorMessage;
    }

}
