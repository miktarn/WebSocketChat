package com.websocket.chat.chat.controller;

import com.websocket.chat.message.domain.Message;
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
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload Message message) {
        if (message.getRoom() != null) {
            log.info(String.format("Message received for room %s from user %s",message.getRoom(), message.getSender()));
            messagingTemplate.convertAndSend("/topic/" + message.getRoom(), message);
        } else {
            log.info("Null room recieved for user "+ message.getSender());
        }
    }

    @MessageMapping("/chat.addUser")
    public void addUser(@Payload Message message, SimpMessageHeaderAccessor headerAccessor) {
        saveUserInfoInSession(message, headerAccessor);

        messagingTemplate.convertAndSend("/topic/" + message.getRoom(), message);
    }

    private static void saveUserInfoInSession(Message message, SimpMessageHeaderAccessor headerAccessor) {
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
