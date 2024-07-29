package com.chatapp.vartalaab.configuration;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;


@Component
public class CustomChannelInterceptor implements ChannelInterceptor {
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        System.out.println("Channel Interceptor");
        
        MessageHeaders headers = message.getHeaders();
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        MultiValueMap<String, String> multiValueMap = headers.get(StompHeaderAccessor.NATIVE_HEADERS,MultiValueMap.class);
        if(multiValueMap == null)
            return message;
        for(Entry<String, List<String>> value : multiValueMap.entrySet() ){
            System.out.println(value.getKey() + " # " + value.getValue());
        }
        return message;
    }
}
