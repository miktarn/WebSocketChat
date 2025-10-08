package com.websocket.chat.message.controller;

import com.websocket.chat.message.ChatMessage;
import com.websocket.chat.message.service.MessageService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/message")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;


    @GetMapping
    public List<ChatMessage> getRoomMessages(@RequestParam String room) {
        return messageService.getAllByRoomChronologically(room);
    }
}
