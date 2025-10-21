package com.websocket.chat.message.controller;

import com.websocket.chat.message.ChatMessage;
import com.websocket.chat.message.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@Slf4j
@RequiredArgsConstructor
public class TopicController {
    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessage message) {
        if (message.getRoom() != null) {
            log.info("ChatMessage received for room {} from user {}",
                    message.getRoom(), message.getSender());
            messagingTemplate.convertAndSend("/topic/" + message.getRoom(), message);
            messageService.persist(message);
        } else {
            log.info("Null room received for user {}", message.getSender());
        }
    }
}
