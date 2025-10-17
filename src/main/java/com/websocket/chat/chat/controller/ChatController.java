package com.websocket.chat.chat.controller;

import com.websocket.chat.chat.domain.DomainChat;
import com.websocket.chat.chat.dto.AddUserToChatRequest;
import com.websocket.chat.chat.dto.CreateChatRequest;
import com.websocket.chat.chat.service.ChatService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @GetMapping
    public List<DomainChat> getChatsByUserName(@RequestParam String name) {
        return chatService.getChatsByUserName(name);
    }





    @PostMapping
    public DomainChat create(@RequestBody CreateChatRequest req) {
        return chatService.create(req.roomName(), req.invitedUsersNames(), req.creatorName());
    }

    @PostMapping("/user")
    public DomainChat addUserToChat(@RequestBody AddUserToChatRequest req) {
        return chatService.addUserToChat(req.chatName(), req.userName());
    }
}
