package com.websocket.chat.websocket.config;

import com.websocket.chat.message.ChatMessage;
import com.websocket.chat.message.MessageType;
import com.websocket.chat.message.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketEventListener {

    private final SimpMessageSendingOperations messageTemplate;
    private final MessageService messageService;

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String username = (String) headerAccessor.getSessionAttributes().get("username");

        if (username == null) {
            log.error("Username must not be null");
            return;
        }
        String chats = (String) headerAccessor.getSessionAttributes().get("chats");
        if (chats != null) {
            for (String room : chats.split(":")) {
                log.info("User disconnected: {}", username);
                var chatMessage = ChatMessage.builder()
                        .type(MessageType.LEAVE)
                        .sender(username)
                        .room(room)
                        .build();
                messageTemplate.convertAndSend("/topic/" + room, chatMessage);
                messageService.persist(chatMessage);
            }
        }
    }
}
