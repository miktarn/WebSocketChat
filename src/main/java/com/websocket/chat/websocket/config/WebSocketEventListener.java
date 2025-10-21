package com.websocket.chat.websocket.config;

import static com.websocket.chat.websocket.config.WebSocketConfig.TOPIC;

import com.websocket.chat.chat.domain.DomainChat;
import com.websocket.chat.chat.service.ChatService;
import com.websocket.chat.message.ChatMessage;
import com.websocket.chat.message.MessageType;
import com.websocket.chat.message.service.MessageService;
import com.websocket.chat.user.service.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketEventListener {

    public static final String USERNAME = "username";
    private final SimpMessageSendingOperations messageTemplate;
    private final MessageService messageService;
    private final UserService userService;
    private final ChatService chatService;

    @EventListener
    public void handleSessionSubscribe(SessionSubscribeEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String destination = accessor.getDestination();

        if (destination != null && destination.startsWith("/invites/")) {
            String username = destination.substring("/invites/".length());
            accessor.getSessionAttributes().put(USERNAME, username);
        }
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String username = (String) headerAccessor.getSessionAttributes().get(USERNAME);

        if (username == null || !userService.exists(username)) {
            log.error("Invalid username during SessionDisconnectEvent processing");
            return;
        }
        List<DomainChat> chats = chatService.getChatsByUserName(username);
        chats.forEach(chat -> sendUserDisconnectedMessage(chat, username));
    }

    private void sendUserDisconnectedMessage(DomainChat chat, String username) {
        log.info("User disconnected: {}", username);
        var chatMessage = ChatMessage.builder()
                .type(MessageType.LEAVE)
                .sender(username)
                .room(chat.getName())
                .build();
        String destinationUrl = TOPIC + "/" + chat.getName();
        messageTemplate.convertAndSend(destinationUrl, chatMessage);
        messageService.persist(chatMessage);
    }
}
