package com.chatapp.vartalaab.dto;

import lombok.Data;

import java.util.Date;

@Data
public class MessageDto {

    private String sender;
    private String receiver;
    private String message;
    private Date timestamp;

    public MessageDto() {
    }

    public MessageDto(String sender, String message, Date timestamp) {
        this.sender = sender;
        this.message = message;
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "{" +
                "sender='" + sender + "', message='" + message + "', timestamp=" + timestamp + "'}";
    }
}
