package com.chatapp.vartalaab.document;

import com.chatapp.vartalaab.dto.MessageDto;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document("Messages")
@Data
public class Message {
    @Id
    private String id;
    List<MessageDto> messages;
}
