This branch is developing websocket connecting using stomp protocol.

CustomChannelInterceptor is for intercepting every request of websocket, it is kept for the intention of authentication the websocket
at the first connect when connect headers is being sent and save the session id for to avoid authentication jwt token everytime.

WebSocketConfig.

registry.addEndpoint("/ws").setAllowedOriginPatterns("*").withSockJS(); - websocket connecttion still need to be appended with /websocket after this endpoint, 
it is kind of required for stomp

@Override
public void configureMessageBroker(MessageBrokerRegistry config) {
    config.enableSimpleBroker("/topic");
    config.setApplicationDestinationPrefixes("/app");
}

Stomp uses a broker along with this websocket connection to distribute messages to clients, 
currently we are using inbuilt broker but an standalone broker can be used.

any url prefix with /topic will go to the queue, {it uses partition internally and we can use /{user} to send messages to particular user - (poc is pending)} 
/app is resvered for application purpose, any messages with this prefix /app will enter into the application - WebSocketController.

WebSocketController
@MessageMapping - it works similar to requestMapping but designed for websockets
@SendTo - broadcasting the messages to the topic.
SimpMessagingTemplate - template to send messages, there are more things offered need to check.


currently the front end and backend code is setup to make a websocket connection.
front end will send a message to application /app/chat,
which in turn will publish a message to /topic/messages to which client is already subscirbe so that messages sent to that client and will be visible in the UI



