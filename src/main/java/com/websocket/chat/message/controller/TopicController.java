package com.websocket.chat.message.controller;

import com.websocket.chat.message.ChatMessage;
import com.websocket.chat.message.service.MessageService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@Log
@RequiredArgsConstructor
public class TopicController {
    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessage message) {
        if (message.getRoom() != null) {
            log.info(String.format("ChatMessage received for room %s from user %s",message.getRoom(), message.getSender()));
            messagingTemplate.convertAndSend("/topic/" + message.getRoom(), message);
            messageService.persist(message);
        } else {
            log.info("Null room recieved for user "+ message.getSender());
        }
    }

    @MessageMapping("/chat.addUser")
    public void addUser(@Payload ChatMessage message, SimpMessageHeaderAccessor headerAccessor) {
        saveUserInfoInSession(message, headerAccessor);
        messagingTemplate.convertAndSend("/topic/" + message.getRoom(), message);
        messageService.persist(message);
    }

    private static void saveUserInfoInSession(ChatMessage message, SimpMessageHeaderAccessor headerAccessor) {
        Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();
        sessionAttributes.put("username", message.getSender());

        String chats = (String) sessionAttributes.get("chats");
        if (chats == null) {
            sessionAttributes.put("chats", message.getRoom());
        } else {
            sessionAttributes.put("chats", String.format("%s:%s", chats, message.getRoom()));
        }
    }
}
