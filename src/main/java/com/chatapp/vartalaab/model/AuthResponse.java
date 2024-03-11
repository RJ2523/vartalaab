package com.chatapp.vartalaab.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class AuthResponse implements Serializable {
    private String jwtToken;

}
