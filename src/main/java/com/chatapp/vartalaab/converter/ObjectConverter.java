package com.chatapp.vartalaab.converter;

import com.chatapp.vartalaab.dto.MessageDto;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class ObjectConverter {
    public DBObject convert(MessageDto source) {
        DBObject dbObject = new BasicDBObject();
        dbObject.put("sender", source.getSender());
        dbObject.put("message", source.getMessage());
        dbObject.put("timeStamp", source.getTimestamp());
        return dbObject;
    }
}
