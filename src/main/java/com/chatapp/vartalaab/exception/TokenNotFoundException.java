package com.chatapp.vartalaab.exception;

import lombok.Data;

@Data
public class TokenNotFoundException extends RuntimeException{
    
    private String errorMessage;
    public TokenNotFoundException(String errorMessage){
        this.errorMessage = errorMessage;
    }

}
